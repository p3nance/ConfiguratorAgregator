---
name: Parser rewrite status
description: Current state of Playwright parser rewrite and remaining work
type: project
---

Playwright-based AbstractHtmlParser was implemented (lazy browser, navigate+wait) but parsing does NOT work — price regexes never match real site HTML (DNS, Citilink, Ozon). User asked to rewrite AbstractHtmlParser.

**Why:** Cloudflare/JS-rendered sites don't expose prices in predictable regex patterns. Need either: (a) wait for specific selectors instead of fixed timeout, (b) use JS evaluation to extract data, or (c) fallback to simpler http-based approach.

**How to apply:** When working on this, try running parsers locally first to see actual HTML. Consider switching from regex-based extraction to Playwright's querySelector/evaluate approach.
