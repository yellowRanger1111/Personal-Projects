// Code for the View Region page.

// Initialize Mapboxgl access token
mapboxgl.accessToken = 'pk.eyJ1IjoidGhpcnR5b25lbWNkNDI5MCIsImEiOiJjanR0amd0ZTUwdnN5NDRxdWx0YjN0dmZzIn0.3PJ4S_-8XdDrLUVR33uToA';

// The following is sample code to demonstrate navigation.
// You need not use it for final app.

let regionIndex = Number(localStorage.getItem(APP_PREFIX + "-selectedRegion"));
let currentRegion = regionList.getRegionByIndex(regionIndex);
let fenceShow = true;

// Check if the currentRegion exist
if (currentRegion !== undefined)
{
    // If a region index was specified, show name in header bar title. This
    // is just to demonstrate navigation.  You should set the page header bar
    // title to an appropriate description of the region being displayed.
    // document.getElementById("headerBarTitle").textContent = regionNames[regionIndex];
    let currentRegion = regionList.getRegionByIndex(regionIndex);
    var map = new mapboxgl.Map({
        container: 'map',
        style: setting.mapStyle,
        zoom: 16,
        center: currentRegion.corners[0]
    }); 
    map.on('load', function () {
        currentRegion.showPoint(map, setting.point);
        currentRegion.showPolygon(map, setting.polygon);
        currentRegion.showFence(map, setting.fenceDistance);

        // Show all the data
        let hud = document.getElementById("hud");
        hud.innerText = `Name: ${currentRegion.name}
                         Datetime: ${currentRegion.datetime}
                         Total fence: ${currentRegion.markersLength}
                         Perimeter: ${currentRegion.perimeter} m
                         Area: ${currentRegion.area} m`;
        let sup = document.createElement("sup");
        sup.innerText = "2";
        hud.appendChild(sup);
    });
}
else
{
    alert('The Region does not exist');
    location.href = "index.html";
}

// Function for going back to index.html and set the regionList local storage
function backButton()
{
    localStorage.setItem(APP_PREFIX + "-regionList", JSON.stringify(regionList.toJSON()));
    location.href = "index.html";
}

// Function to show/unshow the fence from the map
function showFence(el)
{
    if(fenceShow)
    {
        currentRegion.removeAllMarker();
        el.innerHTML = "<i class=\"material-icons md-light\">outlined_flag</i>";
    }
    else
    {
        currentRegion.showFence(map, setting.fenceDistance);
        el.innerHTML = "<i class=\"material-icons md-light\">flag</i>";
    }
    fenceShow = !(fenceShow);
}

// Function to delete currentRegion from regionList
function deleteRegion()
{
    if (confirm("Are you sure want to delete region? "))
    {
        regionList.removeRegion(regionIndex);
        backButton();
    }
}