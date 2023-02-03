




create table if not exists sample(
    id serial PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    data text,
    value int default 0
);


create FUNCTION sample_trigger() RETURNS TRIGGER AS
'
    BEGIN
        IF (SELECT value FROM sample where id = NEW.id ) > 1000
           THEN
           RAISE SQLSTATE ''23503'';
           END IF;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;


create TRIGGER sample_value AFTER insert ON sample
    FOR EACH ROW EXECUTE PROCEDURE sample_trigger();

create table if not exists ZipNumbers(
    zip INT PRIMARY KEY,
    city VARCHAR(64)
);

create table if not exists Company(
    name VARCHAR(64) PRIMARY KEY,
    phoneNumber VARCHAR(64) UNIQUE NOT NULL,
    country VARCHAR(64),
    --city VARCHAR(64),
    zip INT,
    streetInfo VARCHAR(64),
    FOREIGN KEY (zip) REFERENCES ZipNumbers(zip)
);

create table if not exists Product(
    id serial PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    brandName VARCHAR(64),
    description VARCHAR(64)
);

create table if not exists OrderTransaction(
    transactionId serial PRIMARY KEY,
    cname VARCHAR(64),
    pid INTEGER,
    amount FLOAT,
    orderDate DATE,
    FOREIGN KEY(cname) REFERENCES Company(name),
    FOREIGN KEY(pid) REFERENCES Product(id)
);

create table if not exists Produce(
    produceId serial PRIMARY KEY,
    cname VARCHAR(64),
    pid INTEGER,

    capacity FLOAT,
    FOREIGN KEY(cname) REFERENCES Company(name),
    FOREIGN KEY(pid) REFERENCES Product(id)


);

create table Has_Email(

    e_mail VARCHAR(64) PRIMARY KEY,
    cName VARCHAR(64),

    FOREIGN KEY(cName) REFERENCES Company(name)

);

create table Transaction_History(
    transactionId serial PRIMARY KEY,
    cname VARCHAR(64),
    pid INTEGER,
    amount FLOAT,
    orderDate DATE

);
/*
create table if not exists sample(
    id serial PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    data text,
    value int default 0
);
*/

/*
CREATE TRIGGER  capacity_trigger  BEFORE INSERT ON Produce
FOR EACH ROW
BEGIN
IF NEW.amount > NEW.capacity THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'ERROR:
         AGE MUST BE ATLEAST 25 YEARS!';
END IF;
END;
*/



--Note: This is the trigger that is supposed to check if the amount to be produced is
--larger than the production capacity. Unfortunatly it doesn't work.


create FUNCTION capacity_trigger() RETURNS TRIGGER AS
'
BEGIN

         IF((SELECT SUM(OrderTransaction.amount) FROM OrderTransaction
         WHERE OrderTransaction.cname=NEW.cname AND OrderTransaction.pid=NEW.pid)>
         (SELECT Produce.capacity FROM Produce WHERE Produce.cname=NEW.cname AND Produce.pid=NEW.pid)


         )
            THEN
            RAISE SQLSTATE ''23333'';
            END IF;
         RETURN NEW;

    END;
' LANGUAGE plpgsql;

 create TRIGGER max_capacity AFTER insert ON OrderTransaction
     FOR EACH ROW EXECUTE PROCEDURE capacity_trigger();

create FUNCTION transaction_history_trigger() RETURNS TRIGGER AS
'
    BEGIN
        /*transactionId serial PRIMARY KEY,
            cname VARCHAR(64),
            pid INTEGER,
            amount FLOAT,
            orderDate DATE, */
        INSERT INTO Transaction_History (cname, pid, amount, orderDate) VALUES(NEW.cname,NEW.pid,NEW.amount,NEW.orderDate);
        RETURN NEW;


    END;
' LANGUAGE plpgsql;

 create TRIGGER save_transaction_history AFTER insert ON OrderTransaction
     FOR EACH ROW EXECUTE PROCEDURE transaction_history_trigger();

/*

    CHECK ((
        SELECT COUNT(*)
        FROM Produce P, Company C
        WHERE C.name=P.cname AND P.capacity<=(
            SELECT COUNT (*)
                FROM Produce P1
                WHERE P1.cname=C.name; );
            )=0 )

*/