Product (id,brandName,description,name )
PRIMARY KEY (Product)=<id>

ZipNumbers(zip,city)
PRIMARY KEY (ZipNumbers)=<zip>

Company (name,phoneNumber,country,zip,streetInfo)
PRIMARY KEY (Company)=<name>
FOREIGN KEY Company (zip) REFERENCES ZipNumbers(zip)
phoneNumber For Company CAN NOT be NULL


Produce (produceId,pid,cName,capacity)
PRIMARY KEY (Produce)=<produceId>
FOREIGN KEY Produce(pid) REFERENCES Product(id)
FOREIGN KEY Produce(cName) REFERENCES Company(name)
name For Product CAN NOT be NULL

OrderTransaction (transactionId,amount,orderDate,pid,cName)
PRIMARY KEY (OrderTransaction)=<transactionId>
FOREIGN KEY OrderTransaction(pid) REFERENCES Product(id)
FOREIGN KEY OrderTransaction(cName) REFERENCES Company(name)

Has_Email (cName,e_mail)
PRIMARY KEY (Has_Email)=<e_mail>
FOREIGN KEY Has_Email(cName) REFERENCES Company(name)













