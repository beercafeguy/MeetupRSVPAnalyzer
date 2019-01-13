"bin/mongod.exe" --dbpath data

rem start the shell
.\bin\mongo.exe

# RDMBS								MONGO
# databases 						databases
# tables							collections
# row								document
# column							field
# primary key					primary key
# Mongo stores in JSON
# join 							embedded document


> use meetup_db #create database
>db # list database
> db.createCollection("rsvps", {capped : true, size : 6142800 });
> show collections; 
> db.rsvps_general.find()