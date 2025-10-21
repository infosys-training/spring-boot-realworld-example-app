# 🟡 Scenario B: Missing Articles Bug - Solution Guide

## Bug Summary
**Issue**: Articles save successfully but don't appear in main articles listing  
**Root Cause**: Database query changed from LEFT JOIN to INNER JOIN for article_tags  
**Impact**: Only articles with tags appear in listings; articles without tags are invisible  
**Difficulty**: Intermediate  

---

## The Problem

### What Happened:
1. A "performance optimization" commit changed the article listing query
2. The `article_tags` join was changed from LEFT JOIN to INNER JOIN
3. This means only articles that have at least one tag will appear in listings
4. Articles without tags are filtered out completely, even though they exist in the database

### Technical Details:
- **File**: `src/main/resources/mapper/ArticleReadService.xml`
- **Lines**: 32 and 68 (selectArticleIds and countArticle queries)
- **Issue**: `INNER JOIN article_tags` instead of `LEFT JOIN article_tags`
- **Impact**: Articles without tags are excluded from listings but still accessible directly

---

## Investigation Process

### Step 1: Reproduce the Issue
```bash
# Create article without tags
curl -X POST http://localhost:8080/articles \
  -H "Authorization: Token YOUR_TOKEN" \
  -d '{"article": {"title": "Test", "description": "Test", "body": "Test", "tagList": []}}'

# Check if it appears in listings
curl http://localhost:8080/articles
```

**Expected**: Article should appear in listings  
**Actual**: Article missing from listings but accessible via direct URL

### Step 2: Analyze Data Flow
**Key Devin Prompts**:
- "Help me understand how articles are created and then retrieved for listings"
- "Compare the queries for individual article access vs article listings"
- "Show me the recent changes to article-related database queries"

**Investigation Path**:
1. Check `ArticlesApi.java` - API endpoints for creation vs listing
2. Review `ArticleQueryService.java` - Service layer logic
3. Examine `ArticleReadService.xml` - Database queries
4. Compare article creation vs retrieval queries

### Step 3: Identify Root Cause
**Discovery**: The `selectArticleIds` SQL fragment uses INNER JOIN instead of LEFT JOIN
- **Problem Query**: `inner join article_tags AT on A.id = AT.article_id`
- **Correct Query**: `left join article_tags AT on A.id = AT.article_id`

**Why This Breaks**:
- INNER JOIN only returns rows where both tables have matching records
- Articles without tags have no records in `article_tags` table
- Therefore, articles without tags are excluded from the result set

---

## The Fix

### Solution:
Change INNER JOIN back to LEFT JOIN in the article listing queries:

```xml
<!-- In ArticleReadService.xml, line 32 -->
<sql id="selectArticleIds">
    select
    DISTINCT(A.id) articleId, A.created_at
    from
    articles A
    left join article_tags AT on A.id = AT.article_id  <!-- Changed from inner join -->
    left join tags T on T.id = AT.tag_id
    left join article_favorites AF on AF.article_id = A.id
    left join users AU on AU.id = A.user_id
    left join users AFU on AFU.id = AF.user_id
</sql>
```

```xml
<!-- In ArticleReadService.xml, line 68 -->
<select id="countArticle" resultType="java.lang.Integer">
    select
    count(DISTINCT A.id)
    from
    articles A
    left join article_tags AT on A.id = AT.article_id  <!-- Changed from inner join -->
    left join tags T on T.id = AT.tag_id
    left join article_favorites AF on AF.article_id = A.id
    left join users AU on AU.id = A.user_id
    left join users AFU on AFU.id = AF.user_id
```

---

## Verification

### Test the Fix:
```bash
# After applying fix, create article without tags
curl -X POST http://localhost:8080/articles \
  -H "Authorization: Token YOUR_TOKEN" \
  -d '{"article": {"title": "Fixed Test", "description": "Test", "body": "Test", "tagList": []}}'

# Verify it appears in listings
curl http://localhost:8080/articles
```

**Expected Result**: Article should now appear in the main articles listing

---

## Learning Outcomes

### Key Takeaways:
1. **SQL JOIN Types**: Understanding the difference between INNER JOIN and LEFT JOIN
2. **Query Impact**: Small query changes can have significant functional impact
3. **Data Flow Analysis**: Tracing data from creation to retrieval
4. **Performance vs Functionality**: "Optimizations" can introduce bugs

### Database Concepts:
- **LEFT JOIN**: Returns all rows from left table, even if no match in right table
- **INNER JOIN**: Only returns rows where both tables have matching records
- **Query Optimization**: Performance improvements must preserve functional correctness

### Common Devin Collaboration Patterns:
- **Compare working vs broken**: "Why does direct access work but listing doesn't?"
- **Trace data flow**: "Walk me through article creation to listing retrieval"
- **Examine recent changes**: "Show me what changed in the recent deployment"
- **Test hypotheses**: "Help me create test cases to verify this theory"

---

## Postmortem Template

**Incident**: Articles not appearing in main listings after creation  
**Duration**: [Time from bug introduction to fix]  
**Root Cause**: Database query optimization changed LEFT JOIN to INNER JOIN  
**Resolution**: Reverted JOIN type to preserve functional correctness  
**Prevention**: Add integration tests for articles with and without tags  

---

## Facilitator Notes

### Common Participant Approaches:
- ✅ **Good**: Compare individual vs listing queries, check recent changes
- ❌ **Inefficient**: Focus on article creation logic or authentication
- ✅ **Excellent**: Quickly identify JOIN type as likely culprit

### Time Expectations:
- **Fast learners**: 12-18 minutes
- **Average**: 18-25 minutes  
- **Need help**: 25+ minutes

### Intervention Points:
- If stuck after 15 minutes: Guide toward database query comparison
- If focused on wrong area: Suggest examining the difference between working/broken flows
- If found but can't fix: Help with XML query syntax

### Advanced Discussion Points:
- Performance vs correctness trade-offs
- Database indexing strategies
- Integration testing for query changes
- Code review practices for database modifications
