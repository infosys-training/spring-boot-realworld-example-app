# 🟣 Scenario D: Memory Leak Crash

## Your Assignment

**Branch**: `bug/memory-leak-scenario-d`  
**Difficulty**: Expert  
**Estimated Time**: 25-35 minutes  
**Learning Focus**: Memory profiling, resource management, monitoring

---

## The Incident Report

**Priority**: CRITICAL  
**Reported by**: DevOps Team  
**Time**: Multiple occurrences over the past week

### Issue Description:
> "Our production application has been crashing intermittently after running for 6-8 hours. The crashes happen with OutOfMemoryError exceptions, and we're seeing steadily increasing memory usage over time. The application starts fine and performs well initially, but memory consumption grows continuously until the JVM runs out of heap space. This started happening after we deployed the new caching and analytics features last week."

### Additional Context:
- Application crashes with `java.lang.OutOfMemoryError: Java heap space`
- Memory usage increases steadily over time, never decreasing
- Issue occurs only after extended runtime (several hours)
- Performance is good initially, degrades over time
- Started after recent "performance optimization" deployment
- Monitoring shows heap usage climbing from 200MB to 2GB+ before crash
- Garbage collection becomes increasingly frequent but ineffective

### Error Logs:
```
Exception in thread "http-nio-8080-exec-15" java.lang.OutOfMemoryError: Java heap space
    at java.util.ArrayList.grow(ArrayList.java:267)
    at java.util.ArrayList.ensureCapacityInternal(ArrayList.java:237)
    at java.util.ArrayList.add(ArrayList.java:461)
    at java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1660)
```

---

## Your Mission

Use **Ask Devin** and **Devin Sessions** to:

1. **Identify the memory leak source** causing continuous memory growth
2. **Analyze resource management** patterns in the application
3. **Find the root cause** of uncleaned resources or unbounded growth
4. **Implement proper cleanup** using a Devin Session

---

## Getting Started

### Step 1: Setup Your Environment
```bash
git checkout bug/memory-leak-scenario-d
./gradlew bootRun
```

### Step 2: Understand Memory Leaks
Memory leaks in Java typically occur when:
- Objects are referenced but never cleaned up
- Collections grow indefinitely without bounds
- Resources (connections, streams) aren't properly closed
- Caches don't have eviction policies

### Step 3: Investigate Recent Changes
Focus on the new caching and analytics features mentioned in the incident report.

---

## Success Criteria

- [ ] **Memory leak source identified**: Find the specific code causing unbounded memory growth
- [ ] **Root cause understood**: Understand why memory isn't being released
- [ ] **Cleanup implemented**: Fix the resource management issue
- [ ] **Solution verified**: Ensure memory usage remains stable over time

---

## Helpful Devin Prompts to Get Started

```
"Help me understand what could cause memory leaks in a Spring Boot application"

"I'm seeing OutOfMemoryError after extended runtime. What should I investigate first?"

"Show me any caching or collection management code that might grow indefinitely"

"What are the best practices for preventing memory leaks in Java applications?"
```

---

## Investigation Tips

### Understanding Memory Leaks:
- **Memory leak**: Objects that are no longer needed but still referenced
- **Common causes**: Unbounded collections, unclosed resources, static references
- **Symptoms**: Increasing memory usage, frequent GC, eventual OutOfMemoryError

### Areas to Investigate:
1. **Caching mechanisms**: Are caches bounded? Do they have eviction policies?
2. **Collection usage**: Are collections growing without limits?
3. **Resource management**: Are connections/streams properly closed?
4. **Static references**: Are objects held in static collections?
5. **Event listeners**: Are listeners properly removed?

### Memory Analysis Techniques:
- Look for collections that grow indefinitely
- Check for missing cleanup in scheduled tasks
- Examine resource lifecycle management
- Identify objects that should be garbage collected but aren't

---

## Advanced Concepts

This scenario involves:
- **Memory management** in JVM applications
- **Garbage collection** behavior and limitations
- **Resource lifecycle** management
- **Cache eviction** policies and strategies
- **Memory profiling** and leak detection
- **Production monitoring** and alerting

### Memory Leak Patterns:
- **Accumulator Pattern**: Collections that only add, never remove
- **Listener Leak**: Event listeners not properly unregistered
- **Cache Leak**: Caches without size limits or TTL
- **Resource Leak**: Connections/streams not closed in finally blocks

---

## Monitoring and Debugging

In a real production environment, you would use:
- **Memory profilers** (JProfiler, VisualVM, Eclipse MAT)
- **JVM monitoring** (JConsole, JVisualVM)
- **Application metrics** (Micrometer, Prometheus)
- **Heap dumps** for post-mortem analysis

For this workshop, focus on:
- Code analysis and pattern recognition
- Understanding object lifecycle
- Identifying unbounded growth patterns
- Implementing proper cleanup mechanisms

---

**Remember**: Memory leaks can be subtle and may not manifest immediately. They often require sustained load or extended runtime to become apparent. Focus on finding code patterns that could lead to unbounded memory growth.

**Good luck, detective!** 🕵️‍♂️
