// Code for the Record Region page.

// Initialize Mapboxgl access token
mapboxgl.accessToken = 'pk.eyJ1IjoidGhpcnR5b25lbWNkNDI5MCIsImEiOiJjanR0amd0ZTUwdnN5NDRxdWx0YjN0dmZzIn0.3PJ4S_-8XdDrLUVR33uToA';

// Create the mapboxgl.Map object
var map = new mapboxgl.Map({
    container: 'map',
    style: setting.mapStyle,
    zoom: 16,
    center: [0, 0],
    attributionControl : false
});

let userLocation = [0, 0];
let GPSAccuracy = 0;

let undoButton = document.getElementById("undoButton");
let resetButton = document.getElementById("resetButton");
let saveButton = document.getElementById("saveButton");

let currentRegion = new Region();

map.on('load', function () {
    if (navigator.geolocation) 
    {
        navigator.geolocation.watchPosition(function(position){
            userLocation = [position.coords.longitude, position.coords.latitude];
            GPSAccuracy = position.coords.accuracy;
        }, function errorHandler(error){
            // function call back when error
            if (error.code == 1)
            {
                alert("Location access denied by user.");
            }
            else if (error.code == 2)
            {
                alert("Location unavailable.");
            }
            else if (error.code == 3)
            {
                alert("Location access timed out");
            }
            else
            {
                alert("Unknown error getting location.");
            }
        },{   
            //enabling high accuracy mode, refreshes every time and never runout
            enableHighAccuracy: true,
            timeout: Infinity,
            maximumAge: 0
        });
        map.addControl(new mapboxgl.GeolocateControl({  
            positionOptions: { 
                enableHighAccuracy: true
            },
            maxZoom : 5,
            trackUserLocation: true
        }), "top-right");
        map.addControl(new mapboxgl.NavigationControl(), "top-right");
    } 
    else 
    {
        alert("Geolocation is not supported by this browser.");
        location.href = "index.html";
    }
});

// function to get back to index.html
function backButton()
{
    localStorage.setItem(APP_PREFIX + "-regionList", JSON.stringify(regionList.toJSON()));
    location.href = "index.html";
}
// function to set the corner
function setCorner()
{
	if (GPSAccuracy <= setting.GPSAccuracy)
	{
		if(currentRegion.addCorner(userLocation, map))
		{
			currentRegion.showPolygon(map, setting.polygon);
			displayMessage("Corner Successfully added!", 1000);   
        
			undoButton.disabled = false;
            resetButton.disabled = false;  
            saveButton.disabled = (currentRegion.cornersLength < 3);
		}
		else
		{
			displayMessage("Corner can't be place on there!", 2000);
		}
	}
	else
	{
		displayMessage("Accuracy is below minimum accuracy!", 2000);
	}
}

// Set one corner and update the polygons
function undoCorner()
{
    if(currentRegion.removeCorner(currentRegion.cornersLength - 1))
    {
        resetButton.disabled = (currentRegion.cornersLength == 0);
        undoButton.disabled = (currentRegion.cornersLength == 0);
        saveButton.disabled = (currentRegion.cornersLength < 3);
		
		currentRegion.showPolygon(map, setting.polygon);
    }
}

// Function to reset corner
function resetCorner()
{
    resetButton.disabled = true;
    saveButton.disabled = true;
    undoButton.disabled = true;
    currentRegion.removeAllMarker();
    currentRegion.resetCorner();
    currentRegion.showPolygon(map, setting.polygon);
}

// Funtion to save corner
function saveCorner()
{
    if (currentRegion.calculate())
    {
        currentRegion.setDatetime();
        currentRegion.name = prompt("Enter region Name : ");
		regionList.addRegion(currentRegion);
        backButton();
    }
	else
	{
		displayMessage("Need at least 3 corner to compute the area!", 2000);
	}
}