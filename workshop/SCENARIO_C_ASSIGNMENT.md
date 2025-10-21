# 🔴 Scenario C: Favorites Count Race Condition

## Your Assignment

**Branch**: `bug/favorites-race-condition-scenario-c`  
**Difficulty**: Advanced  
**Estimated Time**: 20-30 minutes  
**Learning Focus**: Concurrency issues, database transactions, race conditions

---

## The Incident Report

**Priority**: HIGH  
**Reported by**: QA Team  
**Time**: This morning during load testing

### Issue Description:
> "We're seeing inconsistent favorite counts on articles during our load testing. When multiple users favorite the same article simultaneously, the favorite count becomes incorrect. Sometimes it shows more favorites than actual users who favorited it, and sometimes the count doesn't match the number of favorite records in the database. This only happens under concurrent load - single-user testing works fine."

### Additional Context:
- Issue occurs only under concurrent access (multiple users favoriting simultaneously)
- Single-user favorite/unfavorite operations work correctly
- Database contains duplicate or inconsistent favorite records
- Favorite counts don't match actual favorite relationships
- Problem started after recent "performance improvements" to favorite processing
- Load testing shows the issue consistently with 10+ concurrent users

---

## Your Mission

Use **Ask Devin** and **Devin Sessions** to:

1. **Understand the concurrency issue** in the favorites system
2. **Identify the race condition** causing data inconsistency
3. **Analyze the root cause** of the concurrent access problem
4. **Implement proper synchronization** using a Devin Session

---

## Getting Started

### Step 1: Setup Your Environment
```bash
git checkout bug/favorites-race-condition-scenario-c
./gradlew bootRun
```

### Step 2: Understand the Issue
The race condition occurs when multiple users try to favorite the same article simultaneously. You'll need to simulate concurrent access to reproduce the issue.

### Step 3: Investigate the Favorites System
Start by understanding how favorites are created and managed:
- Article favorite API endpoints
- Database operations for favorites
- Transaction boundaries and locking

---

## Success Criteria

- [ ] **Race condition identified**: Understand the specific concurrency issue
- [ ] **Root cause found**: Pinpoint the code causing the race condition
- [ ] **Synchronization implemented**: Use proper locking or transactions to fix the issue
- [ ] **Solution tested**: Verify the fix handles concurrent access correctly

---

## Helpful Devin Prompts to Get Started

```
"Help me understand how article favorites are implemented in this application"

"I'm seeing inconsistent favorite counts under load. What could cause race conditions in favorite operations?"

"Show me the database operations involved in favoriting an article"

"What are the best practices for handling concurrent access to shared resources in Spring Boot?"
```

---

## Investigation Tips

### Understanding Race Conditions:
- **Race condition**: When the outcome depends on the timing of concurrent operations
- **Common pattern**: Check-then-act operations without proper synchronization
- **Symptoms**: Inconsistent data, duplicate records, incorrect counts

### Areas to Investigate:
1. **Favorite creation logic**: How are favorites added to the database?
2. **Transaction boundaries**: Are operations properly isolated?
3. **Locking mechanisms**: Is there protection against concurrent access?
4. **Database constraints**: Are there unique constraints to prevent duplicates?

### Testing Concurrent Access:
You may need to create test scenarios that simulate multiple users favoriting the same article simultaneously to reproduce the race condition.

---

## Advanced Concepts

This scenario involves:
- **Thread safety** in web applications
- **Database transaction isolation** levels
- **Optimistic vs pessimistic locking**
- **Atomic operations** and synchronization
- **Concurrent programming** patterns

---

**Remember**: Race conditions can be subtle and may not appear in single-threaded testing. Focus on understanding the timing-dependent nature of the bug and how concurrent access can lead to data inconsistency.

**Good luck, detective!** 🕵️‍♂️
