
// contain an Array of array of span element to show the data and button to increase and decrease
let fenceDistanceElements = [document.getElementById("fenceDistance"), [
    document.getElementById("addFenceDistance"), 
    document.getElementById("removeFenceDistance")
]];

let GPSAccuracyElements = [document.getElementById("GPSAccuracy"), [
    document.getElementById("addGPSAccuracy"), 
    document.getElementById("removeGPSAccuracy")
]];

let fillOpacityElements = [document.getElementById("fillOpacity"), [
    document.getElementById("addFillOpacity"), 
    document.getElementById("removeFillOpacity")
]];

let pointRadiusElements = [document.getElementById("pointRadius"), [
    document.getElementById("addPointRadius"), 
    document.getElementById("removePointRadius")
]];

fenceDistanceElements[0].innerText = `Max Fence Distance : ${setting.fenceDistance} m`;
GPSAccuracyElements[0].innerText = `Min GPS Accuracy : ${setting.GPSAccuracy}`;
fillOpacityElements[0].innerText = `Polygon's Opacity : ${setting.polygon["fill-opacity"]}`;
pointRadiusElements[0].innerText = `Point's Radius : ${setting.point["circle-radius"]}`;

// Function for going back to index.html and set the setting local storage
function backButton()
{
    localStorage.setItem(APP_PREFIX + "-setting", JSON.stringify(setting.toJSON()));
    location.href = "index.html";
}

// Function to change the value of setting.fenceDistance
function changeFenceDistance(isIncrease)
{
    if (isIncrease)
    {
        setting.fenceDistance += 0.1;
    }
    else if (setting.fenceDistance > 0)
    {
        setting.fenceDistance -= 0.1;
    }
    setting.fenceDistance = Number(Number(setting.fenceDistance).toFixed(1));
    fenceDistanceElements[0].innerText = `Max Fence Distance : ${setting.fenceDistance}`;
    fenceDistanceElements[1][1].disabled = (setting.fenceDistance <= 0.1);
}

// Function to change the value of setting.GPSAccuracy
function changeAccuracy(isIncrease)
{
    if (isIncrease)
    {
        setting.GPSAccuracy += 1;
    }
    else if (setting.GPSAccuracy > 0)
    {
        setting.GPSAccuracy -= 1;
    }
    GPSAccuracyElements[0].innerText = `Min GPS Accuracy : ${setting.GPSAccuracy}`;
    GPSAccuracyElements[1][1].disabled = (setting.GPSAccuracy <= 0);
}

// Function to change the value of setting.polygon["fill-opacity"]
function changeFillOpacity(isIncrease)
{
    if (isIncrease)
    {
        if(setting.polygon["fill-opacity"] < 1)
        {
            setting.polygon["fill-opacity"] += 0.05;
        }
    }
    else if(setting.polygon["fill-opacity"] > 0)
    {
        setting.polygon["fill-opacity"] -= 0.05;
    }
    setting.polygon["fill-opacity"] = Number(Number(setting.polygon["fill-opacity"]).toFixed(2));
    fillOpacityElements[0].innerText = `Polygon's Opacity : ${setting.polygon["fill-opacity"]}`;
    fillOpacityElements[1][0].disabled = (setting.polygon["fill-opacity"] >= 1);
    fillOpacityElements[1][1].disabled = (setting.polygon["fill-opacity"] <= 0);
}

// Function to change the value of setting.polygon["circle-radius"]
function changePointRadius(isIncrease)
{
    if (isIncrease)
    {
        setting.point["circle-radius"] += 0.5;
    }
    else if(setting.point["circle-radius"] > 0)
    {
        setting.point["circle-radius"] -= 0.5;
    }
    setting.point["circle-radius"] = Number(Number(setting.point["circle-radius"]).toFixed(2));
    pointRadiusElements[0].innerText = `Point's Radius : ${setting.point["circle-radius"]}`;
    pointRadiusElements[1][1].disabled = (setting.point["circle-radius"] <= 0);
}

