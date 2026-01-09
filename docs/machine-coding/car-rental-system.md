# Design a Car Rental System

## 📜 Problem Statement
Design a car rental service that supports:
- Full-day bookings
- Kilometer-based pricing
- Booking overlap checks
- Early return & delayed return handling

---

## 🧠 Design Overview

### Entities
- **Car**
- **Order**
- **Trip**

### Key Rules
- Inclusive date ranges
- Effective end date = max(bookedTill, actualReturnDate)
- Extra kms charged beyond daily free allowance

---

## 🏗️ Design Patterns Used
- **Single Responsibility Principle**
- **Encapsulation**
- (Optional) Strategy for pricing

---

## 💻 Java Implementation

```java
// paste your CarRentalService code here
```
## ⚠️ Edge Cases

- Overlapping bookings

- Early return

- Late return

- Same-day booking

## ⏱️ Complexity

- Booking: O(n) per car

- End Trip: O(1)