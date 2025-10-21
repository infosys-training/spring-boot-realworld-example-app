# Debug Detective Academy - Facilitator Guide

## Workshop Overview
**Duration**: 60 minutes  
**Format**: Individual debugging challenges with Devin  
**Objective**: Train developers to effectively use Devin for systematic bug identification, root cause analysis, and fix implementation

---

## Introduction Section (10 minutes)

### Opening (3 minutes)

**Welcome participants and set context:**

> "Welcome to Debug Detective Academy! Today we're going to simulate real-world debugging scenarios using this Spring Boot application. You'll be working individually with Devin to identify, diagnose, and fix bugs that have been intentionally introduced into the codebase.
> 
> This isn't just about finding bugs - it's about learning to collaborate effectively with AI to solve complex problems under time pressure, just like you would in production incidents."

**Key Learning Objectives:**
- Systematic approach to bug identification in unfamiliar codebases
- Effective Devin prompting strategies for debugging
- Root cause analysis and postmortem writing
- Fix implementation and validation techniques

### Application Walkthrough (4 minutes)

**Demonstrate the working application:**

1. **Start the application** (if not already running):
   ```bash
   ./gradlew bootRun
   ```

2. **Show key endpoints** in browser/curl:
   - **Tags endpoint**: `http://localhost:8080/tags`
   - **Articles listing**: `http://localhost:8080/articles`
   - **User registration**: `POST http://localhost:8080/users`

3. **Explain the application structure**:
   > "This is a RealWorld blog application with:
   > - **User authentication** (JWT-based)
   > - **Article management** (CRUD operations)
   > - **Social features** (comments, favorites, following)
   > - **Tag system** for categorization
   > 
   > The codebase follows Domain-Driven Design principles with clear separation:
   > - `api/` - REST controllers and web layer
   > - `core/` - Business entities and domain logic  
   > - `application/` - Application services and DTOs
   > - `infrastructure/` - Database and external integrations"

4. **Demo core functionality**:
   - Show successful user login with sample credentials
   - Display article creation and listing
   - Demonstrate comment functionality

### Workshop Format Explanation (3 minutes)

**Explain the debugging challenge structure:**

> "In a few minutes, you'll receive a specific debugging scenario. Each scenario represents a different type of real-world incident you might encounter:
> 
> - **Authentication failures**
> - **Data inconsistency issues** 
> - **Performance problems**
> - **Concurrency bugs**
> 
> You'll also receive different levels of initial information - some of you will start with just a branch name, others might get user reports, error logs, or commit messages. This simulates how incidents are reported in real environments."

**Set expectations for the debugging phase:**

1. **Your Mission (35 minutes total)**:
   - **Identify the bug** (10-15 minutes)
   - **Find the root cause** (10-15 minutes) 
   - **Implement and test a fix** (10-15 minutes)

2. **Working with Devin**:
   - Start with exploratory prompts to understand the codebase
   - Use Devin to analyze error patterns and trace execution flows
   - Collaborate on hypothesis testing and fix validation
   - Document your findings for the debrief

3. **Success Criteria**:
   - **Bug Identified**: Can reproduce and explain the issue
   - **Root Cause Found**: Understand why the bug occurs
   - **Fix Implemented**: Working solution that resolves the problem
   - **Postmortem Written**: Brief explanation of cause and solution

**Devin Collaboration Tips:**
> "Remember, Devin is your debugging partner. Be specific in your prompts:
> - 'Help me understand why users can't log in' 
> - 'Analyze this error stack trace and suggest investigation steps'
> - 'Review this code change for potential issues'
> 
> Use Devin's ability to quickly scan large codebases and identify patterns you might miss."

---

## Pre-Challenge Checklist

Before distributing scenarios, ensure each participant has:

- [ ] **Application running** successfully on `http://localhost:8080`
- [ ] **Devin access** confirmed and authenticated
- [ ] **Git repository** cloned and accessible
- [ ] **Basic endpoints** responding (test `/tags` endpoint)
- [ ] **IDE/editor** ready for code exploration

---

## Scenario Distribution

**Individual assignments will be distributed after this introduction.**

Each participant will receive:
1. **Branch name** to checkout
2. **Initial context** (varies by scenario type)
3. **Success criteria** specific to their bug
4. **Time allocation** guidelines

---

## Facilitator Notes

### During Introduction:
- **Keep energy high** - this should feel like an exciting challenge
- **Emphasize learning** over competition
- **Check for questions** about Devin usage or application setup
- **Note any technical issues** for quick resolution

### Transition to Challenge Phase:
- **Distribute scenarios** efficiently (have them pre-prepared)
- **Start timer** once everyone has their assignment
- **Circulate** to help with any immediate setup issues
- **Avoid giving debugging hints** - let Devin be the primary assistant

### Observation Points:
- How quickly participants start productive Devin conversations
- Common prompting patterns that emerge
- Technical blockers that arise
- Collaboration effectiveness between human and AI

---

*Ready to begin the debugging challenges!*
