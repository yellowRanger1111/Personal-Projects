// Code for the main app page (Regions List).

// The following is sample code to demonstrate navigation.
// You need not use it for final app.


htmlRegionList = document.getElementById("regionsList");
for (let loop = 0; loop < regionList.length; loop++)
{
    let currentRegion = regionList.getRegionByIndex(loop);

    // Create the outer span
    outerSpan = document.createElement("span");
    outerSpan.className = "mdl-list__item-primary-content";

    // Create and append first span
    span = document.createElement("span");
    span.innerText = currentRegion.name;
    outerSpan.appendChild(span);

    // Create and append second span
    span = document.createElement("span");
    span.className = "mdl-list__item-sub-title";
    span.innerHTML = `Date & Time: ${currentRegion.datetime}, Area: ${currentRegion.area} m` + "2".sup();
    outerSpan.appendChild(span);

    // Create li element and append the outer span to li
    li = document.createElement("li");
    li.className = "mdl-list__item mdl-list__item--two-line";
    li.onclick = function(){viewRegion(loop)};
    li.appendChild(outerSpan);

    // Append li element to htmlRegionList
    htmlRegionList.appendChild(li);

}

// Create the outer span
outerSpan = document.createElement("span");
outerSpan.className = "mdl-list__item-primary-content";

// Create and append first span
span = document.createElement("span");
span.innerText = "Add New Region +";
span.style.fontSize = "4vw";
span.style.display = "flex";
span.style.justifyContent = 'center';
outerSpan.appendChild(span);

// Create li element and append the outer span to li
li = document.createElement("li");
li.className = "mdl-list__item mdl-list__item--two-line";
li.onclick = function(){location.href = "recordRegion.html"};
li.appendChild(outerSpan);

// Append li element to htmlRegionList
htmlRegionList.appendChild(li);
function viewRegion(regionIndex)
{
    // Save the desired region to local storage so it can be accessed from view region page.
    localStorage.setItem(APP_PREFIX + "-selectedRegion", regionIndex); 
    // ... and load the view region page.
    location.href = 'viewRegion.html';
}
