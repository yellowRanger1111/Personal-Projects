// Shared code needed by the code of all three pages.

// Prefix to use for Local Storage.  You may change this.
var APP_PREFIX = "MCD4290.fencingApp";

// Region Class to keep track of each corner
class Region
{
    // Class Constructor
    constructor()
    {
        this._name = "";
        this._corners = [];
        this._datetime = NaN;
        this._polygon = GeographicLib.Geodesic.WGS84.Polygon();
        this._markers = [];
        this._area = 0;
        this._perimeter = 0;
    }
    
    // Getter for _name
    get name()
    {
        return this._name;
    }

    // Getter for _corners
    get corners()       
    {
            return this._corners;
    }
    // Getter for _corners
    get cornersLength()       
    {
        return this._corners.length;
    }

    // Getter for _markers.length
    get markersLength()
    {
        return this._markers.length;
    }
    
    // Getter for _datetime
    get datetime()
    {
        return this._datetime;
    }

    // Getter for _area
    get area()
    {
        return this._area;
    }

    // Getter for _perimeter
    get perimeter()
    {
        return this._perimeter;
    }

    // Setter for _name
    set name(newName)
    {
        // Check the type of the newName to avoid newName to be something other than string
        if (typeof(newName) === 'string')
        {
            this._name = newName;
        }
    }

    // Function to add new Corner into the region list of corner (_corners)
    addCorner(newCorner, inputMap)
    {
        let lat = newCorner[1];
        let lon = newCorner[0];

        if (this._corners.length > 3)
        {
            // Check if the point is on the wrong location
            if (this._polygon.TestPoint(lat, lon, true, true) < 0)
            {
                // Return false to tell that
                return false;
            }
        }
        this._corners.push(newCorner);
        this._polygon.AddPoint(lat, lon);
        this._markers.push(new mapboxgl.Marker().setLngLat(newCorner).addTo(inputMap));
        return true;
    }

    // Function to delete the last corner from the corners
    removeCorner(index)
    {
        if (index >= 0 && index < this._markers.length)
        {
            this._markers[index].remove();
            this._markers.splice(index, 1);
            this._corners.splice(index, 1);
            this._polygon.Clear();
            for(let loop = 0; loop < this._corners.length; loop++)
            {
                let lat = this._corners[loop][1];
                let lon = this._corners[loop][0];
                this._polygon.AddPoint(lat, lon);
            }
            return true;
        }
        return false
    }
    
    // Function to reset corner
    resetCorner()
    {
        this._corners = [];
        this._polygon.Clear();
    }

    // Function to set the _datetime to current datetime
    setDatetime()
    {
        this._datetime = new Date().toLocaleString();
    }

    // Function to calculate the area and the perimeter of the polygon
    calculate()
    {
        if (this._corners.length < 3)
        {
            return false;
        }
        let result = this._polygon.Compute(true, true).area;
        this._area = Math.abs(result.area).toFixed(2);
        this._perimeter = Math.abs(result.perimeter).toFixed(2);
        return true;
    }
    
    // Function to show all the corners on the inputMap as a points
    showPoint(inputMap, paintSetting)
    {
        for (let loop = 0; loop < this._corners.length; loop++)
        {
            // Add the point into the map
            inputMap.addLayer({
                "id": "corner_" + loop,
                "type": "circle",
                "source": {
                    "type": "geojson",
                    "data": {
                        "type": "FeatureCollection",
                        "features": [{
                            "type": "Feature",
                            "geometry": {"type": "Point", "coordinates": this._corners[loop]}
                        }]
                    }
                },
                "paint": paintSetting
            });
        }
    }

    // Function to show the polygon created by the corners on the inputMap
    showPolygon(inputMap, paintSetting)
    {
        if (this._corners.length >= 3)
        {
            this._corners.push(this._corners[0]);
            // Check if the layer or source is already exist
            if(inputMap.getLayer('region_polygon') !== undefined)
            {
                inputMap.removeLayer('region_polygon');
                inputMap.removeSource('region_polygon');
            }
            // Add polygon layer to the map
            inputMap.addLayer({
                'id': 'region_polygon',
                'type': 'fill',
                'source': {
                    'type': 'geojson',
                    'data': { 
                        'type': 'Feature',
                        'geometry': {'type': 'Polygon', 'coordinates': [this._corners]}
                    }
                },  
                'layout': {},
                'paint': paintSetting
            });
            this._corners.pop();
        }
    }
    // Function to show fence on the map
    showFence(inputMap, fenceDistance)
    {	
		//Reference code: https://geographiclib.sourceforge.io/1.49/js/tutorial-3-examples.html
        //CALCULATE FENCE
        let tempGeod = GeographicLib.Geodesic.WGS84;
		this._corners.push(this._corners[0]);
		for(let j = 0; j < this._corners.length - 1; j++)
		{
			let line = tempGeod.InverseLine(this._corners[j][1], this._corners[j][0], this._corners[j + 1][1], this._corners[j + 1][0]);
			for (let i = 0; i <= Math.ceil(line.s13 / fenceDistance); i++) 
			{
				let coordinate = line.Position(Math.min(fenceDistance * i, line.s13), tempGeod.STANDARD | tempGeod.LONG_UNROLL);
                let lonlat = [coordinate.lon2, coordinate.lat2];
                this._markers.push(new mapboxgl.Marker().setLngLat(lonlat).addTo(inputMap));
			}
		}
		this._corners.pop();
    }

    // Function to remove all the marker from the map and from the array
    removeAllMarker()
    {
        for(let loop = 0; loop < this._markers.length; loop++)
        {
            this._markers[loop].remove();
        }
        this._markers = [];
    }

    // Function for saving into local storage
    toJSON()
    {
        return {
            "name" : this._name, 
            "corners" : this._corners, 
            "datetime" : this._datetime, 
            "area" : this._area,
            "perimeter" : this._perimeter
        };
    }

    // Function to update properties from javascript primitive object
    updateProperties(inputObject)
    {
        this._name = inputObject.name;
        this._corners = inputObject.corners;
        this._area = inputObject.area;
        this._datetime = inputObject.datetime;
        this._perimeter = inputObject.perimeter;
        for (let i = 0; i < inputObject.corners.length; i++)
        {
            let lat = inputObject.corners[i][1];
            let lon = inputObject.corners[i][0];
            this._polygon.AddPoint(lat, lon);
        }
    }
}

// RegionList Class to keep track of all saved region
class RegionList
{
    constructor()
    {
        this._regions = [];
    }

    // Getter for _regions.length
    get length()
    {
        return this._regions.length;
    }
    // Function to access certain index
    getRegionByIndex(index)
    {
        if (index >= this._regions.length || index < 0)
        {
            return null;
        }
        return this._regions[index];
    }

    // Function to add region to the back of this._regions
    addRegion(inputRegion)
    {
        this._regions.push(inputRegion);
    }

    // Funtion to remove speific region based on the index
    removeRegion(index)
    {
        if (index >= this._regions.length || index < 0)
        {
            return false;
        }
        this._regions.splice(index, 1);
        return true;
    }

    // Function to return the object taht can be converted into JSON
    toJSON()
    {
        let region = [];
        for (let loop = 0; loop < this._regions.length; loop++)
        {
            region.push(this._regions[loop].toJSON());
        }
        return {
            "name" : this._name,
            "regions" : region
        };
    }
    // Function to update properties from javascript primitive object
    updateProperties(inputObject)
    {
        this._name = inputObject.name;
        for (let loop = 0 ; loop < inputObject.regions.length; loop++)
        {
            let temp = new Region();
            temp.updateProperties(inputObject.regions[loop]);
            this._regions.push(temp);
        }   
    }
}

class Setting
{
    constructor()
    {
        this._mapStyle = "mapbox://styles/mapbox/dark-v9";
        this._fenceDistance = 4;
        this._GPSAccuracy = 10;
        this._point = {
            "circle-radius": 5, 
            "circle-color": "#ff6961"
        };
        this._polygon = {
            'fill-color': "#088", 
            'fill-opacity': 0.8
        };
    }
    // getter for mapStyle
    get mapStyle()
    {
        return this._mapStyle;
    }

    // getter for fenceDistance
    get fenceDistance()
    {
        return this._fenceDistance;
    }

    // getter for GPSAccuracy
    get GPSAccuracy()
    {
        return this._GPSAccuracy;
    }

    // Getter for Point
    get point()
    {
        return this._point;
    }
    
    // Getter for Polygon
    get polygon()
    {
        return this._polygon;
    }

    // Setter for fenceDistance
    set fenceDistance(newFenceDistance)
    {
        this._fenceDistance = newFenceDistance
    }
    
    // Setter for GPSAccuracy
    set GPSAccuracy(newGPSAccuracy)
    {
        this._GPSAccuracy = newGPSAccuracy;
    }

    // Setter for Point
    set point(newPoint)
    {
        this._point = newPoint;
    }
    
    // Setter for polygon
    set polygon(newpolygon)
    {
        this._polygon = newpolygon;
    }
    // change the object into primitive javascript object that can be converted to JSON
    toJSON()
    {
        return {
            "fenceDistance": this._fenceDistance,
            "GPSAccuracy": this._GPSAccuracy,
            "point": this._point,
            "polygon": this._polygon,
        };
    }
	
    // Function to update properties from javascript primitive object
	updateProperties(inputObject)
    {
        this._fenceDistance = inputObject.fenceDistance;
        this._GPSAccuracy = inputObject.GPSAccuracy;
        this._point = inputObject.point;
        this._polygon = inputObject.polygon;
    }
}

var setting = new Setting();
var regionList = new RegionList()

// Update the setting by get the setting from the local storage
if (localStorage.getItem(APP_PREFIX + "-setting") === null)
{
    localStorage.setItem(APP_PREFIX + "-setting", JSON.stringify(setting.toJSON()));
}
else
{
    setting.updateProperties(JSON.parse(localStorage.getItem(APP_PREFIX + "-setting")));
}

// Update the  regionList by get the setting from the local storage
if (localStorage.getItem(APP_PREFIX + "-regionList") === null)
{
    localStorage.setItem(APP_PREFIX + "-regionList", JSON.stringify(regionList.toJSON()));
}
else
{
    regionList.updateProperties(JSON.parse(localStorage.getItem(APP_PREFIX + "-regionList")));
}