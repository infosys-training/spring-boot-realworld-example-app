# 🟡 Scenario B: Missing Articles Bug

## Your Assignment

**Branch**: `bug/missing-articles-scenario-b`  
**Difficulty**: Intermediate  
**Estimated Time**: 15-25 minutes  
**Learning Focus**: Database debugging, query analysis, performance issues

---

## The Incident Report

**Priority**: MEDIUM  
**Reported by**: Content Team  
**Time**: Yesterday evening after deployment

### User Complaint:
> "I've been writing articles for the blog and they're saving successfully - I can see them in my profile and access them directly by URL. But when I go to the main articles page or try to browse all articles, mine don't show up in the list! Other people's articles are there, but not mine. I've tried refreshing and even logging out and back in. The articles exist because I can share the direct links, but they're invisible in the main feed."

### Additional Context:
- Articles are being created successfully (save operation works)
- Individual article pages load correctly when accessed directly
- Articles don't appear in the main articles listing (`/articles` endpoint)
- Database contains the article data
- No obvious errors in application logs
- Issue started after yesterday's "performance optimization" deployment

---

## Your Mission

Use **Ask Devin** and **Devin Sessions** to:

1. **Investigate the data flow** from article creation to article listing
2. **Identify why** articles save but don't appear in listings
3. **Find the root cause** of the query or database issue
4. **Implement a fix** using a Devin Session

---

## Getting Started

### Step 1: Setup Your Environment
```bash
git checkout bug/missing-articles-scenario-b
./gradlew bootRun
```

### Step 2: Reproduce the Issue
1. Create a new article via the API:
```bash
# First login to get a token
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"user": {"email": "john@example.com", "password": "password123"}}'

# Use the token to create an article
curl -X POST http://localhost:8080/articles \
  -H "Content-Type: application/json" \
  -H "Authorization: Token YOUR_TOKEN_HERE" \
  -d '{"article": {"title": "Test Article", "description": "Test description", "body": "Test body", "tagList": []}}'
```

2. Check if it appears in the articles listing:
```bash
curl http://localhost:8080/articles
```

### Step 3: Start Your Investigation
Begin with **Ask Devin** to understand the article creation and retrieval flow.

---

## Success Criteria

- [ ] **Root cause identified**: Understand exactly why articles don't appear in listings
- [ ] **Database issue found**: Pinpoint the query or schema problem
- [ ] **Fix implemented**: Use a Devin Session to resolve the issue
- [ ] **Solution tested**: Verify articles now appear in listings after creation

---

## Helpful Devin Prompts to Get Started

```
"Help me understand how articles are created and then retrieved for listings in this application"

"I can create articles but they don't show up in the main articles list. What could cause this?"

"Walk me through the database queries involved in article listing vs individual article retrieval"

"Show me the recent changes to article-related database queries"
```

---

## Investigation Tips

- **Compare working vs broken flows**: Individual article access works, but listing doesn't
- **Check database queries**: Look at the SQL queries for article retrieval
- **Examine recent changes**: The issue started after a "performance optimization"
- **Test with different data**: Try creating articles with and without tags

---

**Remember**: This is a database/query optimization issue that affects data retrieval but not data storage. Focus on the differences between how individual articles are fetched vs how article listings are generated.

**Good luck, detective!** 🕵️‍♂️
