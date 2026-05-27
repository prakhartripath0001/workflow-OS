# Bug Report — Workflow OS

## Critical Issues

### 1. **CORS Configuration Bypass** ⚠️ SECURITY
**File**: [backend/src/main/java/com/workflowos/config/WebConfig.java](backend/src/main/java/com/workflowos/config/WebConfig.java)

**Issue**: The hardcoded CORS origins in `WebConfig` override the property-based configuration in `application-dev.properties` and `application-prod.properties`.

```java
// Current (hardcoded, unsafe):
.allowedOrigins(
    "http://localhost:5173",
    "http://localhost:4173",
    "file://"  // TOO PERMISSIVE!
)
```

**Impact**: 
- Dev config allowing `http://localhost:3000` in properties is ignored
- Prod config is completely ignored
- `file://` protocol is too permissive for Electron (any local file can access the API)

**Fix**: Read origins from `spring.web.cors.allowed-origins` property instead of hardcoding:
```java
@Configuration
public class WebConfig {
    @Value("${spring.web.cors.allowed-origins}")
    private String[] allowedOrigins;
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(allowedOrigins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
```

---

### 2. **Password Hash NullPointerException** ⚠️ RUNTIME ERROR
**File**: [backend/src/main/java/com/workflowos/auth/service/AuthService.java](backend/src/main/java/com/workflowos/auth/service/AuthService.java#L90)

**Issue**: When a user is created via OAuth (e.g., Google, GitHub), their `password_hash` is `null`. The login method tries to match against null:

```java
if (!bcrypt.matches(req.getPassword(), user.getPasswordHash())) {
    // NullPointerException if passwordHash is null!
}
```

**Impact**: Users created via OAuth cannot log in with password (expected), but the error handling is poor.

**Fix**: Check if password exists first:
```java
if (user.getPasswordHash() == null || !bcrypt.matches(req.getPassword(), user.getPasswordHash())) {
    throw new IllegalArgumentException("Invalid email or password");
}
```

---

### 3. **Missing Request Validation on DTOs**
**File**: [backend/src/main/java/com/workflowos/auth/controller/AuthController.java](backend/src/main/java/com/workflowos/auth/controller/AuthController.java#L28)

**Issue**: Request bodies aren't validated. Missing `@Valid` annotation:

```java
// Current (unsafe):
@PostMapping("/register")
public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {

// Should be:
@PostMapping("/register")
public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
```

**Impact**: Invalid requests (null fields, malformed data) reach the service layer.

**Fix**: Add `@Valid` to all POST/PUT endpoints and ensure DTOs have `@NotBlank`, `@Email`, etc.

---

## Medium Issues

### 4. **Token Storage Inconsistency** ⚠️ LOGIC BUG
**File**: [frontend/src/services/apiClient.js](frontend/src/services/apiClient.js#L72)

**Issue**: Three different token keys are used:
- `wf_token` (from OAuth/login)
- `slashai_access_token` (fallback in getAccessToken)
- `slashai_refresh_token` (refresh token)

```javascript
export function setTokens(accessToken, refreshToken) {
  sessionStorage.setItem('wf_token', accessToken)
  sessionStorage.setItem('slashai_access_token', accessToken)  // Duplicate!
  if (refreshToken) {
    sessionStorage.setItem('slashai_refresh_token', refreshToken)
  }
}
```

**Impact**: Confusing code, redundant storage, potential sync issues.

**Fix**: Use a single consistent naming convention:
```javascript
export function setTokens(accessToken, refreshToken) {
  sessionStorage.setItem('access_token', accessToken)
  if (refreshToken) {
    sessionStorage.setItem('refresh_token', refreshToken)
  }
}

function getAccessToken() {
  return sessionStorage.getItem('access_token')
}

function getRefreshToken() {
  return sessionStorage.getItem('refresh_token')
}
```

---

### 5. **Unsafe Logout Implementation**
**File**: [backend/src/main/java/com/workflowos/auth/controller/AuthController.java](backend/src/main/java/com/workflowos/auth/controller/AuthController.java#L48)

**Issue**: The logout endpoint accepts a token in the request body instead of reading from the `Authorization` header:

```java
@PostMapping("/logout")
public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> body) {
    String token = body.get("token");  // Client sends their own token!
    // Vulnerable to token injection
}
```

**Impact**: A malicious client could log out other users or manipulate token invalidation.

**Fix**: Extract token from the Authorization header like the `/me` endpoint:
```java
@PostMapping("/logout")
public ResponseEntity<Map<String, String>> logout(
        @RequestHeader(value = "Authorization", required = false) String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(401).body(Map.of("message", "Missing Authorization header"));
    }
    String token = authHeader.substring(7);
    authService.logout(token);
    return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
}
```

---

### 6. **URL Parameter Extraction Without Validation**
**File**: [frontend/src/App.jsx](frontend/src/App.jsx#L77)

**Issue**: OAuth callback parameters are extracted but not validated:

```javascript
useEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const token  = params.get('wf_token')
    const id     = params.get('wf_id')
    const name   = params.get('wf_name')
    const email  = params.get('wf_email')

    if (token && email) {  // Only checks token & email, not id or name
        const userData = { id, name, email, token }  // id/name could be null
        sessionStorage.setItem('wf_user', JSON.stringify({ id, name, email }))
        // Later: user.name?.split(' ')[0] could fail
    }
}, [])
```

**Impact**: If backend sends malformed params, the app can crash when accessing `user.name.split()`.

**Fix**: Validate all required fields:
```javascript
if (token && email && name && id) {
    const userData = { id, name, email, token }
    // ... safe to use
}
```

---

### 7. **Hardcoded Timeout Values**
**File**: [frontend/src/services/apiClient.js](frontend/src/services/apiClient.js#L10)

**Issue**: API client has a fixed 15-second timeout that doesn't account for slow networks:

```javascript
const apiClient = axios.create({
  timeout: 15_000,  // Hardcoded
})
```

**Fix**: Make configurable via environment:
```javascript
const apiClient = axios.create({
  timeout: parseInt(import.meta.env.VITE_API_TIMEOUT || '15000', 10),
})
```

---

## Minor Issues

### 8. **Missing Flyway Configuration**
**File**: [backend/src/main/resources/application-prod.properties](backend/src/main/resources/application-prod.properties#L23)

**Issue**: The prod config references Flyway migrations:
```properties
spring.flyway.enabled=true
spring.flyway.validate-on-migrate=true
```

But the `pom.xml` doesn't include the Flyway dependency.

**Impact**: Production will fail to start with "ClassNotFoundException: org.flywaydb.core.Flyway".

**Fix**: Either remove Flyway config (since you're using `spring.sql.init`), or add the dependency to `pom.xml`:
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

---

### 9. **Missing @Transactional on Read-Only Operations**
**File**: [backend/src/main/java/com/workflowos/auth/service/AuthService.java](backend/src/main/java/com/workflowos/auth/service/AuthService.java#L112)

**Issue**: Some operations already have `@Transactional(readOnly = true)`, which is good, but consistency should be verified across all services.

---

### 10. **CommandPalette Missing Error Handling**
**File**: [frontend/src/components/CommandPalette.jsx](frontend/src/components/CommandPalette.jsx#L32)

**Issue**: The submit handler doesn't catch errors:
```javascript
async function submit(event) {
    event.preventDefault()
    if (!input.trim()) return
    const response = await commandRouter.execute(input)  // No try-catch!
    setResult(response)
}
```

**Impact**: If `commandRouter.execute()` throws, the component will crash.

**Fix**: Add error handling:
```javascript
async function submit(event) {
    event.preventDefault()
    if (!input.trim()) return
    try {
        const response = await commandRouter.execute(input)
        setResult(response)
    } catch (error) {
        setResult({ error: error.message })
    }
}
```

---

## Recommendations Summary

| Severity | Issue | Status |
|----------|-------|--------|
| 🔴 Critical | CORS hardcoded origins | ⚠️ Security risk |
| 🔴 Critical | Password hash NPE | ⚠️ Runtime crash |
| 🟠 High | Missing @Valid | ⚠️ Input validation |
| 🟠 High | Token storage inconsistent | ⚠️ Logic bug |
| 🟠 High | Unsafe logout | ⚠️ Security risk |
| 🟡 Medium | URL params unvalidated | ⚠️ Crash risk |
| 🟡 Medium | Hardcoded timeout | ⚠️ UX issue |
| 🔵 Low | Flyway misconfiguration | ⚠️ Prod startup |
| 🔵 Low | CommandPalette error handling | ⚠️ Crash risk |

---

## Next Steps

1. Fix the **CORS** and **password hash** bugs immediately (critical)
2. Add **request validation** (@Valid) to all DTOs
3. Standardize **token storage** keys
4. Fix the **logout** endpoint to use Authorization header
5. Validate **OAuth callback parameters**
6. Either add Flyway or remove its config

