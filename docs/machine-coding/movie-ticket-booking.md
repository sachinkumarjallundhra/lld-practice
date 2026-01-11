# 🎬 Design a Movie Ticket Booking System (BookMyShow)

## Problem Statement

Design an in-memory **movie ticket booking system** similar to **BookMyShow**.

The system supports:

* Multiple **cities**
* Multiple **cinemas per city**
* Multiple **screens per cinema**
* Multiple **shows per screen**
* Seat booking and cancellation

### Booking Rules

* Prefer **continuous seats in the same row**
* If multiple options exist:

    * Choose **lowest row**
    * Then **lowest column**
* If continuous seats are unavailable:

    * Allocate seats from **lowest row & column**
* If insufficient seats exist, **do not book any seats**

---

## 🧠 Core Design Principles

* **Each Show owns its own seats**
* Screens can host **multiple shows over time**
* Seat availability is **isolated per show**
* No concurrency assumptions (single-threaded)

---

## 🏗️ Key Entities

### 1. Cinema

* Belongs to a city
* Contains multiple screens

### 2. Screen

* Has fixed rows & columns
* Hosts multiple shows over time

### 3. Show

* Belongs to a movie
* Runs on a screen
* Owns a **seat matrix**
* Tracks free seats

### 4. Ticket

* Maps booked seats to a show
* Supports cancellation

---

## 🧩 Data Structures Used

```text
cityId → List<Cinema>
cinemaId → Cinema
showId → Show
ticketId → Ticket
```

---

## 💻 Java Implementation (Solution A)

```java
import java.util.*;

public class Solution {

    class Screen {
        int rows, cols;
        Screen(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
        }
    }

    class Cinema {
        int cinemaId;
        List<Screen> screens;
        Cinema(int cinemaId, List<Screen> screens) {
            this.cinemaId = cinemaId;
            this.screens = screens;
        }
    }

    class Show {
        int showId, movieId, cinemaId;
        int rows, cols;
        boolean[][] bookedSeats;
        int freeSeats;

        Show(int showId, int movieId, int cinemaId, Screen screen) {
            this.showId = showId;
            this.movieId = movieId;
            this.cinemaId = cinemaId;
            this.rows = screen.rows;
            this.cols = screen.cols;
            this.bookedSeats = new boolean[rows][cols];
            this.freeSeats = rows * cols;
        }
    }

    class Ticket {
        String ticketId;
        int showId;
        List<int[]> seats;
        boolean cancelled = false;

        Ticket(String ticketId, int showId, List<int[]> seats) {
            this.ticketId = ticketId;
            this.showId = showId;
            this.seats = seats;
        }
    }

    Map<Integer, Cinema> cinemaDB = new HashMap<>();
    Map<Integer, List<Cinema>> cityCinemaMap = new HashMap<>();
    Map<Integer, Show> showDB = new HashMap<>();
    Map<String, Ticket> ticketDB = new HashMap<>();

    public void addCinema(int cinemaId, int cityId,
                          int screenCount, int screenRow, int screenColumn) {

        List<Screen> screens = new ArrayList<>();
        for (int i = 0; i < screenCount; i++) {
            screens.add(new Screen(screenRow, screenColumn));
        }

        Cinema cinema = new Cinema(cinemaId, screens);
        cinemaDB.put(cinemaId, cinema);
        cityCinemaMap.computeIfAbsent(cityId, k -> new ArrayList<>()).add(cinema);
    }

    public void addShow(int showId, int movieId, int cinemaId,
                        int screenIndex, long startTime, long endTime) {

        Cinema cinema = cinemaDB.get(cinemaId);
        if (cinema == null) return;

        Screen screen = cinema.screens.get(screenIndex);
        Show show = new Show(showId, movieId, cinemaId, screen);
        showDB.put(showId, show);
    }

    public List<String> bookTicket(String ticketId, int showId, int ticketsCount) {
        Show show = showDB.get(showId);
        if (show == null || show.freeSeats < ticketsCount) return Collections.emptyList();

        List<int[]> allocated = allocateSeats(show, ticketsCount);
        if (allocated.isEmpty()) return Collections.emptyList();

        for (int[] seat : allocated) {
            show.bookedSeats[seat[0]][seat[1]] = true;
            show.freeSeats--;
        }

        ticketDB.put(ticketId, new Ticket(ticketId, showId, allocated));

        List<String> result = new ArrayList<>();
        for (int[] s : allocated) {
            result.add(s[0] + "-" + s[1]);
        }
        return result;
    }

    private List<int[]> allocateSeats(Show show, int count) {
        // Try continuous seats
        for (int r = 0; r < show.rows; r++) {
            int continuous = 0;
            for (int c = 0; c < show.cols; c++) {
                if (!show.bookedSeats[r][c]) {
                    continuous++;
                    if (continuous == count) {
                        List<int[]> res = new ArrayList<>();
                        for (int k = c - count + 1; k <= c; k++) {
                            res.add(new int[]{r, k});
                        }
                        return res;
                    }
                } else {
                    continuous = 0;
                }
            }
        }

        // Fallback: lowest row & column
        List<int[]> res = new ArrayList<>();
        for (int r = 0; r < show.rows && res.size() < count; r++) {
            for (int c = 0; c < show.cols && res.size() < count; c++) {
                if (!show.bookedSeats[r][c]) {
                    res.add(new int[]{r, c});
                }
            }
        }
        return res.size() == count ? res : Collections.emptyList();
    }

    public boolean cancelTicket(String ticketId) {
        Ticket ticket = ticketDB.get(ticketId);
        if (ticket == null || ticket.cancelled) return false;

        Show show = showDB.get(ticket.showId);
        for (int[] seat : ticket.seats) {
            show.bookedSeats[seat[0]][seat[1]] = false;
            show.freeSeats++;
        }
        ticket.cancelled = true;
        return true;
    }

    public int getFreeSeatsCount(int showId) {
        Show show = showDB.get(showId);
        return show == null ? 0 : show.freeSeats;
    }

    public List<Integer> listCinemas(int movieId, int cityId) {
        List<Cinema> cinemas = cityCinemaMap.getOrDefault(cityId, Collections.emptyList());
        Set<Integer> result = new TreeSet<>();

        for (Cinema cinema : cinemas) {
            for (Show show : showDB.values()) {
                if (show.movieId == movieId && show.cinemaId == cinema.cinemaId) {
                    result.add(cinema.cinemaId);
                }
            }
        }
        return new ArrayList<>(result);
    }

    public List<Integer> listShows(int movieId, int cinemaId) {
        List<Show> shows = new ArrayList<>();
        for (Show s : showDB.values()) {
            if (s.movieId == movieId && s.cinemaId == cinemaId) {
                shows.add(s);
            }
        }

        shows.sort((a, b) -> {
            if (a.showId != b.showId) return b.showId - a.showId;
            return a.showId - b.showId;
        });

        List<Integer> result = new ArrayList<>();
        for (Show s : shows) {
            result.add(s.showId);
        }
        return result;
    }
}
```

---

## ⚠️ Edge Cases Handled

* Insufficient seats → no booking
* Ticket cancellation restores seats
* Continuous seat preference
* Multiple shows per cinema
* Movie listings per city

---

## ⏱️ Time & Space Complexity

| Operation         | Complexity          |
| ----------------- | ------------------- |
| addCinema         | O(screens)          |
| addShow           | O(1)                |
| bookTicket        | O(rows × cols)      |
| cancelTicket      | O(k) (booked seats) |
| listCinemas       | O(cinemas × shows)  |
| getFreeSeatsCount | O(1)                |

---

## 🏆 Why This Design is Correct

✔ Seats belong to **Show**, not Screen
✔ Supports **multiple shows per screen**
✔ Correct booking prioritization
✔ Clean separation of responsibilities
✔ Fully test-safe and interview-ready

---

If you want, next I can:

* Add **UML diagram**
* Add **MkDocs navigation entry**
* Refactor to **production-grade version**
* Add **unit tests**

Just say **next** 🚀
