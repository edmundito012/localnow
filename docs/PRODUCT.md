# LocalNow — Product Definition

## Vision

LocalNow helps people find a nearby, trusted professional who is genuinely available when the work is needed.

The first version focuses on urgent home repairs in Madrid. It is deliberately narrow so that we can validate real demand and build marketplace liquidity before expanding to more cities or categories.

## Problem

Finding a local professional is possible through search engines, directories, referrals, and lead-generation platforms. The difficult part is finding someone who:

- performs the exact service required;
- operates near the customer;
- is available in the requested time window;
- has a trustworthy work history;
- can assess the request with enough context;
- accepts the customer's expected budget.

Customers often contact several professionals and wait for replies. Professionals, meanwhile, receive poorly qualified leads that may be too far away, outside their schedule, or missing essential information.

## Initial Market

- **City:** Madrid, Spain
- **Use case:** urgent and same-day home repairs
- **Initial categories:**
  - Plumbing
  - Electrical work
  - Locksmith services

Expansion to other categories or cities must be justified by completed-job data and user interviews.

## Users

### Customer

A person who needs a home repair and values speed, clarity, trust, and predictable communication.

### Professional

An independent professional or small local business that wants qualified jobs matching its skills, service area, and real availability.

## Core Value Proposition

### For customers

Submit one structured request and receive relevant professionals ranked by availability, distance, service fit, reputation, and budget compatibility.

### For professionals

Receive actionable requests with the location, time window, problem description, photos, and budget information needed to make a quick decision.

## Core Customer Journey

1. The customer describes the problem and may attach photos.
2. The customer selects the location, required time, and estimated budget.
3. LocalNow filters eligible professionals by category, service area, verification, and availability.
4. Matching ranks the eligible professionals.
5. The customer sends a request.
6. A professional accepts.
7. Customer and professional communicate about the job.
8. The professional completes the service.
9. Payment is recorded.
10. Both sides can close the job and the customer leaves a review.

## MVP Scope

The first usable release includes:

- customer and professional registration;
- role-based authentication;
- professional profiles and basic verification status;
- service categories and professional skills;
- professional service areas and availability;
- structured service requests with location, time window, budget, and photos;
- geospatial candidate search;
- explainable matching and ranking;
- request acceptance with concurrency protection;
- basic job lifecycle;
- job-scoped chat and notifications;
- payment integration;
- customer reviews;
- basic operational observability.

## Explicitly Out of Scope for V1

- nationwide or international launch;
- dozens of service categories;
- iOS and web clients;
- microservices;
- Kubernetes;
- Kafka or event sourcing;
- dynamic pricing;
- subscriptions;
- advanced machine-learning matching;
- live worker tracking;
- complex dispute automation.

These may be reconsidered only after the main customer journey works with real users.

## Initial Matching Strategy

Candidate generation must first apply hard eligibility rules:

- correct service category;
- inside the professional's service area;
- available in the requested time window;
- active and sufficiently verified;
- no conflicting confirmed job.

Eligible candidates can then be ranked using an explainable weighted score based on:

- availability fit;
- distance;
- service and skill fit;
- rating and review confidence;
- budget compatibility;
- historical response rate;
- completion reliability.

Weights are product hypotheses and must be configurable and evaluated against real outcomes.

## Business Model Hypothesis

LocalNow will initially test a commission on completed jobs.

- **Starting hypothesis:** 10–15% of the final transaction value
- No charge for registration or browsing during early validation
- Payment and commission rules must be transparent before a professional accepts a job

This is a hypothesis, not a final pricing decision. Interviews and completed transactions will determine whether commission, subscription, lead fees, or a hybrid model is viable.

## Success Metrics

### North-star metric

**Completed jobs per week**

### Supporting metrics

- percentage of requests receiving at least one qualified candidate;
- median time to first professional response;
- request-to-acceptance conversion;
- acceptance-to-completion conversion;
- cancellation rate by reason;
- repeat-customer rate;
- active professionals completing at least one job;
- contribution margin per completed job.

Downloads, page views, and registrations are diagnostic metrics, not proof of product value.

## First Validation Milestone

Before broad development, interview at least five professionals from the initial categories in Madrid.

Validate:

- how they currently acquire work;
- what makes a lead worth responding to;
- what information they need before accepting;
- how they communicate availability and service area;
- what they currently pay for customer acquisition;
- whether they would pay a commission for a completed job.

## First Product Milestone

Demonstrate one complete flow using realistic data:

```text
Customer creates request
        ↓
PostGIS finds eligible professionals
        ↓
Matching ranks candidates
        ↓
Professional accepts exactly once
        ↓
Job moves through its lifecycle
        ↓
Service is completed
```

The milestone is complete only when the flow is tested end to end, not when isolated endpoints exist.

## Product Principles

- Start with one dense local market.
- Prefer completed jobs over vanity metrics.
- Make matching explainable.
- Collect only the data required to complete a service safely.
- Introduce infrastructure when a real product problem requires it.
- Keep irreversible architectural decisions to a minimum.
- Validate assumptions with users continuously.
