# Repository Description


## Details
- Team Name: Database - Admin Client
- Website URL: TBD

## Team Members, Roles:
Project Manger: <Jakob S, jakob.saalfrank@grasshopperairmobility.com>||Assist PM: <Daniel F, daniel.furmanov@grasshopperairmobility.com>
Database: Zachary Treichler

## Project Documentation
1. [MVP MASTER DOCUMENT](https://docs.google.com/document/d/1Il3FX-ej5XE0wotqH0wCDc-4ZzkNH2Q2KDrhLl-BJCE/edit?tab=t.mo7laqriq1a0)
2. [Code Base Practices](https://docs.google.com/document/d/1C2W3vkSYP1dXtUuyjuNoRq-N1Rifpowr07Ta6i7JMrI/edit?tab=t.0#heading=h.37o44pmzag1k)
1. [System Diagram](https://lucid.app/lucidchart/1d77d2aa-6200-437c-9c93-4405de7612d3/edit?viewport_loc=-657%2C-445%2C2482%2C1308%2C0_0&invitationId=inv_d7f08602-18cb-4e15-85b4-b4268503c3d0)
2. [Database Relational Diagram](https://lucid.app/lucidchart/055b16fd-897d-48e0-be49-d61bba3be3e8/edit?viewport_loc=253%2C-778%2C3348%2C1765%2CNAtjpjORU~jZ&invitationId=inv_4e618e2f-9607-4a65-8243-819cbc73c7bc)
4. [API Routes](https://docs.google.com/document/d/1OEmvmq6I1vybHsgix0aWJ-XW2czCuAHlC4gkgXD9ooM/edit?tab=t.0#heading=h.asieuksr1yyy)
5. [Database Documentation](https://docs.google.com/document/d/1Cap1Ggfbc0cZdWnnkJB9BLb-PZIgyjSJumrl7SeMnHg/edit?tab=t.0)

## How to run the admin client
1. See required software in the database documentation
2. CD into the admin-cli folder
3. Run the command "export env DATABASE_URI=*INSERT URI HERE FOR LOGISTICS DATABASE*"
4. Run the command "export env TEST_URI=*INSERT URI HERE FOR TESTING DATABASE*"
5. Run the command "mvn clean package compile"
6. Run the command "mvn exec:java"
