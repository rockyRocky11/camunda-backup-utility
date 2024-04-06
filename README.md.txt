Steps to run the camunda backup utility
1. download the jar from build\libs\camunda-backup-utility.jar

2.open command prompt and run the following command(replace the properties before you run the command)

java -jar camunda-backup-utility.jar --camundaHosturl=http://localhost:8081 --camundaUsername=demo --camundaPassword=demo  --startDate=2024-04-03 --endDate=2024-04-04 --fileStoragePath=C:\\Users\\vivek\\

camundaHosturl= camunda server hostname
camundaUsername= Camunda login username
camundaPassword= camunda login password
startDate= start date in date range filer
endDate= end date in date range filer
fileStoragePath= preferably users folder