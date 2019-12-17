# OsMowSis

Assignment 7 
Group 35

### How to run

1. Place test files under ./back-end/osmowsis/src/main/java/resources/static/scenarios/
2. In one terminal, go to ./back-end/osmowsis/ and run `mvn spring-boot:run`
3. In another separate terminal, go to ./front_end/ and run `npm start` => this should bring up the browser
4. When ready, enter the test file name (including .csv) in the form area and click `Submit Test Case` => Use appropriate buttons to run the simulation from there
5. **To run another test case**, restart the backend (ctrl+c in step 2 then `mvn spring-boot:run` again), then redo step 4. The front end does not need to be restarted.


#### Notes

* Hover over a mower and a tooltip will say its current energy level
* The allow CORS plugin should be installed and turned on for the front end to communicate with the backend. This should already be done in the submitted VM image by default
* Virtual machine and design documents are available at https://drive.google.com/drive/folders/1p9rwCAz8V9-81SbL914zavzFy36qhTDD
