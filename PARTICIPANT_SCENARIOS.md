# Debug Detective Academy - Participant Scenarios

## Your Mission 🎯

You've been assigned a debugging scenario in this Spring Boot RealWorld application. Work with Devin to **identify**, **diagnose**, and **fix** the bug within **35 minutes**.

---

## Workshop Bug Scenarios

### 🟢 **Scenario A: Authentication Failure**
**Difficulty**: Beginner | **Estimated Time**: 10-15 minutes  
**Learning Focus**: Environment configuration, security debugging

**The Bug**:
- Users can't log in to the application
- JWT token validation fails consistently
- Login endpoint returns authentication errors

**Root Cause**: Secret key misconfiguration in `application.properties`

**Devin Collaboration Strategy**:
- Analyze authentication flow and JWT token handling
- Review security configuration files
- Check environment variables and property settings
- Trace login request through security filters

---

### 🟡 **Scenario B: Missing Articles Bug**
**Difficulty**: Intermediate | **Estimated Time**: 15-25 minutes  
**Learning Focus**: Database debugging, query analysis, performance issues

**The Bug**:
- Articles save successfully but don't appear in listings
- Database contains the data but queries return incomplete results
- No error messages, just missing content

**Root Cause**: Missing database index or query optimization issue

**Devin Collaboration Strategy**:
- Trace article creation and retrieval flows
- Analyze database queries and execution plans
- Review repository implementations and SQL queries
- Investigate data persistence vs. data retrieval logic

---

### 🔴 **Scenario C: Favorites Count Race Condition**
**Difficulty**: Advanced | **Estimated Time**: 20-30 minutes  
**Learning Focus**: Concurrency issues, database transactions, race conditions

**The Bug**:
- Article favorites count becomes inconsistent under load
- Multiple users favoriting simultaneously causes data corruption
- Count doesn't match actual favorite records in database

**Root Cause**: Concurrent access to favorite count without proper locking

**Devin Collaboration Strategy**:
- Analyze concurrent access patterns in favorite functionality
- Review transaction boundaries and isolation levels
- Investigate database locking mechanisms
- Test race condition scenarios and synchronization

---

### 🟣 **Scenario D: Memory Leak Crash**
**Difficulty**: Expert | **Estimated Time**: 25-35 minutes  
**Learning Focus**: Memory profiling, resource management, monitoring

**The Bug**:
- Application crashes after running for extended periods
- Memory usage continuously increases over time
- Performance degrades before eventual crash

**Root Cause**: Unclosed database connections or caching issue

**Devin Collaboration Strategy**:
- Analyze resource lifecycle and cleanup patterns
- Review database connection management
- Investigate caching implementations and memory usage
- Use profiling techniques to identify memory leaks

---

## Your Debugging Toolkit

### **Phase 1: Investigation (10-15 minutes)**
1. **Gather Context with Ask Devin**
   ```
   "Help me understand the codebase structure for [feature area]"
   "Analyze this error message and suggest investigation steps"
   "What are the key components involved in [functionality]?"
   ```

3. **Form Initial Hypotheses and document**
   - What could cause this specific symptom?
   - Which code areas are most likely involved?

### **Phase 2: Root Cause Analysis (10-15 minutes)**
1. **Deep Dive with Ask Devin**
   ```
   "Trace the execution flow for [specific operation]"
   "Review this code section for potential issues"
   "What could cause [specific behavior] in this context?"
   ```

2. **Identify the Root Cause**
   - Pinpoint the exact line(s) of code causing the issue
   - Understand why the bug occurs

### **Phase 3: Fix & Validate (10-15 minutes)**
1. **Implement Solution with Devin Session**
   - Use your Ask Devin Sessions to have Devin construct a prompt for you to implement the fix
   - Use the "Devin Session" feature to implement the fix
   - Document the fix in the debrief
   ```
   "Help me implement a fix for [root cause]"
   "Review this proposed solution for correctness"
   "What edge cases should I consider?"
   ```

2. **Review the PR**
   - Click on the PR at the end of the Devin Session or go to https://github.com/infosys-training/spring-boot-realworld-example-app
   - Look at the new changes in the commits

3. **Document Your Solution**
   - Brief explanation of the root cause
   - Description of your fix
   - Any additional considerations

---

## Success Criteria ✅

By the end of your session, you should have:

- [ ] **Identified the root cause** with specific code location
- [ ] **Used Devin to implement a working fix** that resolves the issue
- [ ] **Written a brief postmortem** explaining cause and fix

---

## Effective Devin Prompting Tips

### **🎯 Be Specific**
❌ "This doesn't work"  
✅ "Users can't log in - the POST /users/login endpoint returns 401 even with correct credentials"

### **🔍 Request Analysis**
❌ "Fix this bug"  
✅ "Analyze this stack trace and help me understand what's causing the NullPointerException"

### **📋 Ask for Structure**
❌ "Show me the code"  
✅ "Walk me through the authentication flow from login request to JWT token validation"

### **🧪 Collaborate on Testing**
❌ "Is this right?"  
✅ "Help me create a test case to verify this fix handles edge case X correctly"

---

## Scenario-Specific Debugging Patterns

### **Authentication Issues** 🟢
1. Check JWT secret key configuration in `application.properties`
2. Verify security filter chain and authentication flow
3. Review token generation and validation logic
4. Test with known good credentials and examine logs

### **Data Retrieval Issues** 🟡
1. Compare data persistence vs. retrieval query logic
2. Check database indexes and query performance
3. Verify repository method implementations
4. Analyze SQL execution plans and optimization

### **Concurrency Issues** 🔴
1. Identify shared state in favorites functionality
2. Review transaction boundaries and locking strategies
3. Test concurrent access scenarios
4. Implement proper synchronization mechanisms

### **Memory Management Issues** 🟣
1. Trace resource allocation and cleanup patterns
2. Monitor database connection pool usage
3. Analyze caching behavior and memory retention
4. Use profiling tools to identify leak sources