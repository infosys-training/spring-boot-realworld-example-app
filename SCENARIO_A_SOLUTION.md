# 🟢 Scenario A: Authentication Failure - Solution Guide

## Bug Summary
**Issue**: Users cannot log in after JWT secret key rotation  
**Root Cause**: JWT secret key was changed, invalidating all existing tokens and preventing proper token validation  
**Difficulty**: Beginner  

---

## The Problem

### What Happened:
1. A "security enhancement" commit rotated the JWT secret key
2. The new secret key (`-ROTATED` suffix added) doesn't match tokens issued with the old key
3. Existing user sessions became invalid
4. New login attempts fail because the application tries to validate against the wrong secret

### Technical Details:
- **File**: `src/main/resources/application.properties`
- **Line**: 9 (`jwt.secret` property)
- **Issue**: Secret key was changed from original to `...6wA-ROTATED`
- **Impact**: JWT token signing and validation mismatch

---

## Investigation Process

### Step 1: Reproduce the Issue
```bash
# Try to login with sample credentials
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"user": {"email": "john@example.com", "password": "password123"}}'
```
**Expected**: Should return user data with JWT token  
**Actual**: Returns authentication error

### Step 2: Analyze JWT Configuration
**Key Devin Prompts**:
- "Help me understand how JWT authentication works in this Spring Boot application"
- "Show me where JWT tokens are generated and validated"
- "What could cause JWT validation to fail for existing users?"

**Investigation Path**:
1. Check `DefaultJwtService.java` - JWT implementation
2. Review `application.properties` - Configuration values
3. Compare recent commits - What changed?

### Step 3: Identify Root Cause
**Discovery**: The `jwt.secret` property was modified in recent commit
- Original: `...TiuDapkLiUCogO3JOK7kwZisrHp6wA`
- Modified: `...TiuDapkLiUCogO3JOK7kwZisrHp6wA-ROTATED`

---

## The Fix

### Solution Options:

#### Option 1: Revert Secret Key (Quick Fix)
```properties
# In application.properties, change back to:
jwt.secret=nRvyYC4soFxBdZ-F-5Nnzz5USXstR1YylsTd-mA0aKtI9HUlriGrtkf-TiuDapkLiUCogO3JOK7kwZisrHp6wA
```

#### Option 2: Proper Secret Rotation (Production Approach)
1. Keep the new secret key
2. Implement graceful token migration
3. Add support for multiple valid secrets during transition period
4. Force all users to re-authenticate

### Recommended Fix for Workshop:
**Revert the secret key** to restore immediate functionality:

```bash
# Edit application.properties
# Remove the "-ROTATED" suffix from jwt.secret
```

---

## Verification

### Test the Fix:
```bash
# After applying fix, test login again
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"user": {"email": "john@example.com", "password": "password123"}}'
```

**Expected Result**: Should return JSON with user data and valid JWT token

---

## Learning Outcomes

### Key Takeaways:
1. **Configuration Management**: Small config changes can have big impacts
2. **JWT Security**: Secret key rotation requires careful planning
3. **Debugging Process**: Systematic investigation from symptoms to root cause
4. **Production Considerations**: Secret rotation needs migration strategy

### Common Devin Collaboration Patterns:
- **Start broad**: "Help me understand the authentication system"
- **Get specific**: "Show me where JWT secrets are configured"
- **Trace execution**: "Walk through the token validation process"
- **Verify solution**: "Help me test that login works after this change"

---

## Postmortem Template

**Incident**: Authentication failure after deployment  
**Duration**: [Time from bug introduction to fix]  
**Root Cause**: JWT secret key rotation without proper token migration  
**Resolution**: Reverted secret key to restore compatibility  
**Prevention**: Implement proper secret rotation procedures with gradual migration  

---

## Facilitator Notes

### Common Participant Approaches:
- ✅ **Good**: Start with understanding JWT flow, check recent changes
- ❌ **Inefficient**: Focus on user credentials or database issues
- ✅ **Excellent**: Quickly identify configuration as likely culprit

### Time Expectations:
- **Fast learners**: 8-12 minutes
- **Average**: 12-18 minutes  
- **Need help**: 18+ minutes

### Intervention Points:
- If stuck after 10 minutes: Guide toward configuration files
- If focused on wrong area: Suggest checking recent commits
- If found but can't fix: Help with property file editing
