# Solara Resort Booking and Management System
CLI management system in Java 17+ (Apache NetBeans). Data is saved in text files.
Payments are simulated only (no real money).

## Run
Open the project in Apache NetBeans, set Main Class to `solara.Main`, press F6.
Or from a terminal in this folder:

    javac -d out src/solara/*.java
    java -cp out solara.Main

## Demo accounts (created automatically on first run)
| Role     | Username | Password  |
|----------|----------|-----------|
| Admin    | admin    | solara123 |
| Staff    | staff    | staff123  |
| Customer | guest    | guest123  |

Customers can also create their own account from the welcome menu.

## Roles
- Customer: make a reservation, track a booking, pay (simulated)
- Staff: view/search bookings, confirm, check in, record payments
- Admin: everything staff can do + complete, cancel, delete bookings, manage user accounts

## Data files (folder `data/`, created automatically)
reservations.txt, users.txt, prices.txt. Delete the folder to reset everything.

## Status
Done: login/logout, 3 roles, reservations (create/view/search/update/delete),
payments, user account management.
TODO (teammates): edit prices, statistics, CSV export.
