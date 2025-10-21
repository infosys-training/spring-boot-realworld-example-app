# 🟢 Scenario A: Authentication Failure Bug

## Your Assignment

**Branch**: `bug/auth-failure-scenario-a`  
**Difficulty**: Beginner  
**Estimated Time**: 10-15 minutes  
**Learning Focus**: Environment configuration, security debugging

---

## The Incident Report

**Priority**: HIGH  
**Reported by**: Customer Support Team  
**Time**: This morning after deployment

### User Complaint:
> "I was logged into the blog this morning and everything was working fine. But after lunch, when I tried to access my profile, it says I'm not authenticated anymore! I didn't log out, and I know I was still logged in because I had the tab open. Now when I try to use any features, it keeps asking me to log in again. What happened to my session?"

### Additional Context:
- Multiple users reporting sudden session invalidation
- Users were logged in before deployment, now sessions are invalid
- New logins work, but existing sessions are broken
- Deployment happened during lunch time
- Application logs show JWT validation errors

---

## Your Mission

Use **Ask Devin** and **Devin Sessions** to:

1. **Identify the root cause** of the authentication failure
2. **Understand why** existing user credentials stopped working
3. **Implement a fix** using a Devin Session
4. **Document your findings** for the postmortem

---

## Getting Started

### Step 1: Setup Your Environment
```bash
git checkout bug/auth-failure-scenario-a
./gradlew bootRun
```

### Step 2: Reproduce the Issue
Try to log in with the sample credentials from the README:
- **Username**: johndoe  
- **Email**: john@example.com  
- **Password**: password123

### Step 3: Start Your Investigation
Begin with **Ask Devin** to understand the authentication flow and identify what might have changed.

---

## Success Criteria

- [ ] **Root cause identified**: Pinpoint exactly what's causing the authentication failure
- [ ] **Fix implemented**: Use a Devin Session to resolve the issue
- [ ] **Solution tested**: Verify users can log in successfully after your fix
- [ ] **Postmortem written**: Brief explanation of what went wrong and how you fixed it

---

## Helpful Devin Prompts to Get Started

```
"Help me understand how JWT authentication works in this Spring Boot application"

"I'm seeing authentication failures after a recent deployment. What should I check first?"

"Walk me through the login flow from the API endpoint to token validation"
```

---

**Remember**: This is a realistic scenario that could happen in production. Focus on systematic debugging and clear communication with Devin about what you're investigating and why.

**Good luck, detective!** 🕵️‍♂️
