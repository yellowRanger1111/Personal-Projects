// The CameraVideoPageController is a class that controls the camera 
// video page.  This class provides a some useful methods you will
// need to call:
//     cameraVideoPage.displayMessage(message, timeout):
//         Causes a short message string to be displayed on the
//         page for a brief period.  Useful for showing quick
//         notifications to the user.  message is a plain string.
//         timeout is option and denotes the length of time in msec
//         to show the message for.
//     cameraVideoPage.setHeadsUpDisplayHTML(html):
//         This will set or update the heads-up-display with the
//         text given in the html argument.  Usually this should 
//         just be a string with text and line breaks (<br />).

// Initialise the camera video page and callback to our 
// cameraVideoPageInitialised() function when ready.
var cameraVideoPage = new CameraVideoPageController(
        cameraVideoPageInitialised);


// You may need to create variables to store state.
class Position
{
    constructor()
    {
        // Initialized all the attributes to be NaN
        this.userHeight = NaN;
        this.objectDistance = NaN;
        this.time = NaN;
        this.objectHeight = NaN;
        this.baseAngle = NaN;
        this.apexAngle = NaN;
    }

    calculateDistance()
    {
        // Function to calculate the distance from the person to the target by using base angle and user's height
        // Check if the userHeight and baseAngle is already filled or not
        if (isNaN(this.userHeight) || isNaN(this.baseAngle))
        {
            // Return False if userHeight or baseAngle is still Nan (not filled yet)
            return false;
        }
        this.objectDistance = this.userHeight * (Math.tan(this.baseAngle * Math.PI / 180));
        return true;
    }

    calculateObjectHeight()
    {
        // Function to calculate the target height by using object's distance, apex angle and user's height
        // Check if the objectDistance and baseAngle is already filled or not
        if (isNaN(this.objectDistance) || isNaN(this.apexAngle))
        {
            // Return False if objectDistance or baseAngle is still Nan (not filled yet)
            return false;
        }
        this.objectHeight = this.objectDistance * (Math.tan(this.apexAngle * Math.PI / 180)) + this.userHeight;
        return true;
    }

    setTime()
    {
        // Function to Set the time
        this.time = new Date();
    }
};

// An array for current position and previous position
let positions = [new Position(), new Position(), new Position()];

// An array that store recent pitch values
let recentPitch = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0];

// A variable that store average pitch
let averagePitch = 0;

// Button Properties, track and element 
// Used to dynamically change the button name and function when user click back or forward button
let buttonsProperties = [
    ['Set Cam Height', setCameraHeightValue],
    ['Set Base', setBaseTiltAngle],
    ['Set Apex', setApexTiltAngle],
    ['The Result', calculateResult],
    ['Estimated Time', calculateTime],
];
let buttons = [document.getElementById('button-1'), document.getElementById('button-2')];
let buttonTrack = 1;

// A function to handle device motion
function deviceMotionHandler(e)
{
    // Pitch calculations, based on:
    // https://eng1003.monash/apps/sensortest

    let gY = e.accelerationIncludingGravity.y / 9.8;
    let pitch = Math.atan(-e.accelerationIncludingGravity.y / e.accelerationIncludingGravity.z);
    
    // Convert Pitch to Degree
    pitch *= 180/Math.PI;

    // Convert the pitch to 0 - 360
    if (pitch > 0)
    {
        pitch = 180 - pitch;
        if (gY <= 0)
        {
            pitch = 180 + pitch;
        }
    }
    else
    {
        pitch = 0 - pitch;
        if (gY <= 0)
        {
            pitch = 180 + pitch;
        }
    }

    // Update the averagePitch
    averagePitch -= recentPitch[0]/10;
    averagePitch += pitch/10;
    averagePitch = averagePitch;

    // Update the pitch array
    recentPitch.shift();
    recentPitch.push(pitch);

    // Show all the data to the screen
    outputString = ""
    if (document.getElementById('switch-1').checked)
    {
        outputString = "Angle: " + averagePitch.toFixed(2).toString() + "\n";
        outputString += "Camera Height: " + positions[2].userHeight.toString() + "\n";
        outputString += "Base Angle: " + positions[2].baseAngle.toFixed(2).toString() + "\n";
        outputString += "Apex Angle: " + positions[2].apexAngle.toFixed(2).toString();
    }
    document.getElementById('hud').innerText = outputString;
}   


// This function will be called when the camera video page
// is intialised and ready to be used.
function cameraVideoPageInitialised()
{
    // Check devicemotion availability and intialise deviceMotion
    if (window.DeviceMotionEvent)
    {
        window.addEventListener('devicemotion', deviceMotionHandler)
    }
    else
    {
        alert("Your device doesn't support devicemotion! ");
    }
}
    

// This function is called by a button to set the height of phone from the
// ground, in metres.
function setCameraHeightValue()
{
    // Set camera height
    // Temporary variable for storing user's height
    do {
        // Ask for input
        positions[2].userHeight = Number(prompt("Please enter your body height in centimeter : "));

        // check if input is a number and is positive
        if (isNaN(positions[2].userHeight))
        {
            alert("Insert a number please!");
        }
        else if (positions[2].userHeight <= 10)
        {
            alert("Body height at least 10 centimeters");
        }

    } while(positions[2].userHeight <= 0 || isNaN(positions[2].userHeight));

    // Decrease the user's height by 10 cm (estimated distance from top of head to eyes)
    positions[2].userHeight -= 10;

    // display on screen using the displayMessage method
    cameraVideoPage.displayMessage("Your Handphone height to ground (centimetre): " + positions[2].userHeight.toString(), 1000);
}
    

// This function is called by a button to set the angle to the base of
// the object being measured.  It uses the current smoothed tilt angle.
function setBaseTiltAngle()
{
    // Record base tilt angle 
    positions[2].baseAngle = averagePitch;

    // display on screen using the displayMessage method
    cameraVideoPage.displayMessage("Base Angle : " +  averagePitch.toFixed(2).toString(), 1000);
}


// This function is called by a button to set the angle to the apex of
// the object being measured.  It uses the current smoothed tilt angle.
function setApexTiltAngle()
{
    // Record apex tilt angle 
    positions[2].apexAngle = averagePitch - 90;

    // display on screen using the displayMessage method
    cameraVideoPage.displayMessage("Apex Angle : " + averagePitch.toFixed(2).toString(), 1000);
}


// You may need to write several other functions.
function calculateResult()
{
    // Check if function to calculate distance run successfully
    if (positions[2].calculateDistance())
    {
        cameraVideoPage.displayMessage("Estimated Object's Distance (centimetre): " + positions[2].objectDistance.toFixed(2).toString(), 1250);

        // Check if function to calculate object height run successfully
        if (positions[2].calculateObjectHeight())
        {
            cameraVideoPage.displayMessage("Estimated Object's Height (centimetre): " + positions[2].objectHeight.toFixed(2).toString(), 1250);

            // Set the time
            positions[2].setTime();
    
            // Delete the oldest position
            positions.shift();
    
            // Add new position
            positions.push(new Position());

            // Alert them
            positions[2].userHeight = positions[1].userHeight;
            cameraVideoPage.displayMessage("The base and apex angle have been reset", 1000);
        }
        else
        {
            cameraVideoPage.displayMessage("Please set the apex angle", 1500);
        }
    }
    else
    {
        cameraVideoPage.displayMessage("Please set the base angle", 1500);
    }
}


// Function to calculate estimated time
function calculateTime()
{
    // Check if the time is set or not
    if (!(isNaN(positions[0].time) || isNaN(positions[1].time)))
    {
        // calculate the difference of time and distance between previous and current position
        let deltaTime = positions[1].time - positions[0].time;
        let deltaDistance = positions[0].objectDistance - positions[1].objectDistance;

        // check if the deltaDistance is positive or not
        if (deltaDistance > 0)
        {
            // Speed is on centimeters/millisecond
            let speed = deltaDistance / deltaTime;      

            // Calculate the estimated time and convert it to seconds
            let estimatedTime = (positions[1].objectDistance / (speed * 1000)).toFixed(2);

            // Display the estimated time
            cameraVideoPage.displayMessage("Estimated Time to Object (seconds): " + estimatedTime.toString(), 2000);
        }
        else
        {
           cameraVideoPage.displayMessage("Please do not walk backward", 2000);   
        }
    }
    else  
    {
        cameraVideoPage.displayMessage("Please at least measure two times at different place", 2000);
    }
}


// Function to change the button text and function
function backButton(el)
{
    // Move the buttonTrack down by 1
    if (buttonTrack > 1)
    {
        buttonTrack--;

        // Update the text
        buttons[0].innerText = buttonsProperties[buttonTrack - 1][0];
        buttons[1].innerText = buttonsProperties[buttonTrack][0];

        // Update the onclick event handler
        buttons[0].onclick = buttonsProperties[buttonTrack - 1][1];
        buttons[1].onclick = buttonsProperties[buttonTrack][1];

        // Update the color
        // Reference for the color : https://getmdl.io/components/index.html#buttons-section
        document.getElementById("forward").className = "mdl-button mdl-js-button mdl-button--fab mdl-button--mini-fab mdl-button--primary";
        if (buttonTrack === 1)
        {
            el.className = "mdl-button mdl-js-button mdl-button--fab mdl-button--mini-fab mdl-button--accent";
        }
    }
}

function forwardButton(el)
{
    // Move the buttonTrack up by 1
    if (buttonTrack < buttonsProperties.length - 1)
    {
        buttonTrack++;
        
        // Update the text
        buttons[0].innerText = buttonsProperties[buttonTrack - 1][0];
        buttons[1].innerText = buttonsProperties[buttonTrack][0];

        // Update the onclick event handler
        buttons[0].onclick = buttonsProperties[buttonTrack - 1][1];
        buttons[1].onclick = buttonsProperties[buttonTrack][1];

        // Update the color
        // Reference for the color : https://getmdl.io/components/index.html#buttons-section
        document.getElementById("back").className = "mdl-button mdl-js-button mdl-button--fab mdl-button--mini-fab mdl-button--primary";
        if (buttonTrack === buttonsProperties.length - 1)
        {
            el.className = "mdl-button mdl-js-button mdl-button--fab mdl-button--mini-fab mdl-button--accent";
        }
    }
}

function showInformation()
{
    let output =  "Welcome to Height Measuring App\n";
    output += "Step 1 : Set your body Height.\n";
    output += "Step 2 : Put your Phone in front of your eyes.\n";
    output += "Step 3 : Press Set Base when bottom of the\n";
    output += "            object right in the crosshair.\n"
    output += "Step 4 : Press Set Apex when top of the\n";
    output += "            object right in the crosshair.\n"
    output += "Step 5 : Press Show Result to show\n";
    output += "            the object's distance and height\n";
    output += "Step 6 : Move a couple step ahead\n";
    output += "Step 7 : Repeat step 2-7 and press Estimated\n";
    output += "            Time to see the Estimated Time need\n";
    output += "            to arrive on the object\n";
    output += "          - Owen, Calvin and Mukul -";
    alert(output);
}