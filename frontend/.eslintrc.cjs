// =============================================================================
// ESLint Configuration — SlashAI Frontend
// Extends: react/recommended + react-hooks + import order
// =============================================================================

module.exports = {
  root: true,
  env: {
    browser: true,
    es2022: true,
    node: true,
  },
  parser: '@babel/eslint-parser',
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
    ecmaFeatures: { jsx: true },
    requireConfigFile: false,
    babelOptions: { presets: ['@babel/preset-react'] },
  },
  extends: [
    'eslint:recommended',
    'plugin:react/recommended',
    'plugin:react/jsx-runtime',      // Suppress "React must be in scope" for React 17+
    'plugin:react-hooks/recommended',
    'plugin:import/recommended',
    'prettier',                       // Disables ESLint rules that conflict with Prettier
  ],
  plugins: ['react', 'react-hooks', 'import'],
  settings: {
    react: { version: 'detect' },
    'import/resolver': { node: { extensions: ['.js', '.jsx'] } },
  },
  rules: {
    // ── React ──────────────────────────────────────────────────────────────
    'react/prop-types': 'warn',
    'react/display-name': 'off',
    'react/no-unused-prop-types': 'warn',

    // ── Hooks ──────────────────────────────────────────────────────────────
    'react-hooks/rules-of-hooks': 'error',
    'react-hooks/exhaustive-deps': 'warn',

    // ── Import ordering ────────────────────────────────────────────────────
    'import/order': [
      'warn',
      {
        groups: ['builtin', 'external', 'internal', 'parent', 'sibling', 'index'],
        'newlines-between': 'always',
        alphabetize: { order: 'asc', caseInsensitive: true },
      },
    ],
    'import/no-unresolved': 'error',
    'import/no-duplicates': 'error',

    // ── General ────────────────────────────────────────────────────────────
    'no-console': ['warn', { allow: ['warn', 'error'] }],
    'no-unused-vars': ['warn', { argsIgnorePattern: '^_', varsIgnorePattern: '^_' }],
    'no-var': 'error',
    'prefer-const': 'error',
    eqeqeq: ['error', 'always'],
    curly: ['error', 'all'],
  },
  overrides: [
    // Electron main process files — allow Node.js globals
    {
      files: ['electron/**/*.js'],
      env: { node: true, browser: false },
      rules: { 'no-console': 'off' },
    },
    // Test files
    {
      files: ['**/*.test.js', '**/*.test.jsx', '**/*.spec.js'],
      env: { jest: true },
      rules: { 'no-unused-vars': 'off' },
    },
  ],
}
