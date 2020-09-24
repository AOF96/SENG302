<template>
  <div>
    <div id="locationSettingsMap"></div>
    <v-container>
      <v-row justify="center" align="center"><h4 class="selectLocationText">Select on map or type in the box</h4></v-row>
    </v-container>
    <div class="locationFieldDiv">
      <v-text-field v-model="address" id="activityLocationAutocomplete" class="locationInput" label="Address" outlined dense></v-text-field>
    </div>
  </div>
</template>

<script>
  import mapStyles from "../../../util/mapStyles";

  export default {
    name: "ActivityLocationSettings",
    props: ['darkModeGlobal', 'location'],
    data() {
      return {
        map: null,
        marker: null,
        address: "",
        localLocation: null
      }
    },

    computed: {
      locationCopy: {
        get: function() {
          if(this.localLocation === null){
            return this.location;
          }else{
            return this.localLocation;
          }
        },
        set: function(newValue) {
          this.localLocation = newValue;
        }
      }
    },

    mounted() {
      this.loadMap();
    },

    methods: {
      /**
       * Creates an event handler to check if the theme has changed
       * @param map
       */
      setThemeCheckEvent(map) {
        let outer = this;
        window.google.maps.event.addDomListener(window, 'click', function() {
          map.setOptions({
            styles: mapStyles[outer.darkModeGlobal ? "dark" : "light"]
          });
        });
      },

      /**
       * Loads the map onto the page and centres on the users home city.
       * Adds a marker on the city's centre.
       */
      loadMap() {
        if (!window.google) {
          return;
        }

        let self = this;

        this.geocoder = new window.google.maps.Geocoder();

        let position = null;
        let zoomLevel = 2;

        if (this.locationCopy) {
          zoomLevel = 10;
          position = new window.google.maps.LatLng(this.locationCopy.latitude, this.locationCopy.longitude);
        } else {
          position = new window.google.maps.LatLng(0, 0);
        }

        self.map = new window.google.maps.Map(document.getElementById("locationSettingsMap"), {
          center: position,
          zoom: zoomLevel,
          streetViewControl: false,
          fullscreenControl: false,
          rotateControl: false,
          mapTypeControl: false,
          styles: mapStyles[this.darkModeGlobal ? "dark" : "light"]
        });

        self.map.addListener('click', function (e) {
          if (self.marker != null) {
            self.marker.setMap(null);
          }
          self.marker = new window.google.maps.Marker({
            position: e.latLng,
            map: self.map
          });
          self.map.panTo(e.latLng);
          self.getLocationFromLatLng(e.latLng);
        });

        this.setMapCentre();
        this.loadLocationAutocomplete();
        this.setThemeCheckEvent(self.map);
        this.updateAddressString(this.locationCopy);
      },

      /**
       * Centres the map on the users current location
       */
      setMapCentre() {
        let self = this;
        if (this.locationCopy) {
          const latLng = new window.google.maps.LatLng(this.locationCopy.latitude, this.locationCopy.longitude);
          self.marker = new window.google.maps.Marker({
            position: latLng,
            map: self.map
          });
        }
      },

      /** load google autocomplete on create activity page **/
      loadLocationAutocomplete() {
        if (!window.google) {
          return;
        }
        this.autocomplete = new window.google.maps.places.Autocomplete(
            document.getElementById("activityLocationAutocomplete"),
            {
              types: ["address"],
            }
        );

        this.autocomplete.addListener("place_changed", this.fillInAddress);
      },

      /**
       * Fills in address field and location object from address autocomplete
       */
      async fillInAddress() {
        let self = this;
        const place = this.autocomplete.getPlace();
        this.locationCopy = await this.extractLocationData(place);
        this.updateAddressString(this.locationCopy);
        this.sendLocationToParent();
        if (this.marker != null) {
          this.marker.setMap(null);
        }
        this.marker = new window.google.maps.Marker({
          position: place["geometry"]["location"],
          map: self.map
        });
        self.map.panTo(place["geometry"]["location"]);
        self.sendLocationToParent();
      },

      /**
       * Extractor function that parses the google maps response and returns a location object.
       */
      async extractLocationData(place) {
        let newLocation = {street_address:"",suburb:"",postcode:"",city:"",state:"",country:"",latitude:"",longitude:""};
        let addressComponents = place["address_components"];
        newLocation["latitude"] = place["geometry"]["location"].lat();
        newLocation["longitude"] = place["geometry"]["location"].lng();

        for (let i = 0; i < addressComponents.length; i++) {
          let content = addressComponents[i]["long_name"];
          if(addressComponents[i]["types"].includes("subpremise")){newLocation.street_address += content+"/";}
          if(addressComponents[i]["types"].includes("street_number")){newLocation.street_address += content;}
          if(addressComponents[i]["types"].includes("route")){newLocation.street_address += " "+content}
          if(addressComponents[i]["types"].includes("sublocality")){newLocation.suburb = content}
          if(addressComponents[i]["types"].includes("locality")){newLocation.city = content}
          if(addressComponents[i]["types"].includes("administrative_area_level_1")){newLocation.state = content}
          if(addressComponents[i]["types"].includes("country")){newLocation.country = content}
          if(addressComponents[i]["types"].includes("postal_code")){newLocation.postcode = content}
        }
        newLocation.street_address = newLocation.street_address.trim();

        return newLocation;
      },

      /**
       * Calls google maps api with lat lng and updates the location object
       */
      getLocationFromLatLng(latlng) {
        let thisInner = this;
        this.geocoder.geocode({'location': latlng}, async function (results, status) {
          if (status === 'OK') {
            if(results.length === 0){
              this.snackbarText = "Invalid Location";
              this.snackbarColour = "error";
              this.snackbar = true;
            }else{
              thisInner.locationCopy = await thisInner.extractLocationData(results[0]);
              thisInner.updateAddressString(thisInner.locationCopy);
              thisInner.sendLocationToParent();
            }
          } else {
            this.snackbarText = status;
            this.snackbarColour = "error";
            this.snackbar = true;
          }
        });
      },

      /**
       * Updates the address string from the locationObject parameter. Used when the location is changed by clicking
       * on the map, and when the map is first loaded with the initial location.
       */
      updateAddressString(locationObject) {
        if (locationObject) {
          this.address = "";
          if (locationObject.street_address !== "") {
            this.address += locationObject.street_address
          }
          if (locationObject.suburb !== "") {
            if (this.address !== "") {
              this.address += ", "
            }
            this.address += locationObject.suburb;
          }
          if (locationObject.city !== "") {
            if (this.address !== "") {
              this.address += ", "
            }
            this.address += locationObject.city;
          }
          if (locationObject.state !== "") {
            if (this.address !== "") {
              this.address += ", "
            }
            this.address += locationObject.state;
          }
          if (locationObject.country !== "") {
            if (this.address !== "") {
              this.address += ", "
            }
            this.address += locationObject.country;
          }
        }
      },

      /**
       * Sends the location to the parent component
       */
      sendLocationToParent() {
        this.$emit('set-location', this.localLocation);
      }
    }
  }
</script>

<style scoped>
.selectLocationText{
  padding-top:20px;
  color: var(--v-primaryText);
}
</style>