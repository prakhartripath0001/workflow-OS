// =============================================================================
// Domain Exception Classes
// =============================================================================
package com.workflowos.common.exception;

/** Thrown when a requested resource does not exist in the database. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found with id: " + id);
    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
