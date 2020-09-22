<template>
  <div>
    <div id="locationSettingsMap"></div>
    <hr/>
    <v-container>
      <v-row justify="center" align="center"><h4 class="selectLocationText">Select on map or type in the box</h4></v-row>
    </v-container>
    <div class="locationFieldDiv">
      <v-text-field v-model="address" id="activityLocationAutocomplete" class="locationInput" label="Address" outlined dense></v-text-field>
    </div>
  </div>
</template>

<script>
  export default {
    name: "ActivityLocationSettings",
    data() {
      return {
        marker: null,
        location: {
          street_address: null,
          suburb: null,
          postcode: null,
          city: null,
          state: null,
          country: null,
          latitude: null,
          longitude: null,
        },
        address: ""
      }
    },

    mounted() {
      this.loadMap();
    },

    methods: {
      /**
       * Loads the map onto the page and centres on the users home city.
       * Adds a marker on the city's centre.
       */
      loadMap() {
        if (!window.google) {
          return;
        }
        this.geocoder = new window.google.maps.Geocoder();

        let map = new window.google.maps.Map(document.getElementById("locationSettingsMap"), {
          zoom: 9,
          streetViewControl: false,
          fullscreenControl: false,
          rotateControl: false,
          mapTypeControl: false
        });

        let outer = this;

        map.addListener('click', function (e) {
          if (outer.marker != null) {
            outer.marker.setMap(null);
          }
          outer.marker = new window.google.maps.Marker({
            position: e.latLng,
            map: map
          });
          map.panTo(e.latLng);
          outer.getLocationFromLatLng(e.latLng);
        });

        this.setMapCentre(map);
        this.loadLocationAutocomplete();
      },

      /**
       * Centres the map on the users current location
       */
      setMapCentre(map) {
        let outer = this;
        let address = this.location.street_address + ' ' + this.location.city + ' ' + this.location.country;
        let latLng = new window.google.maps.LatLng(this.location.latitude, this.location.longitude);
        this.geocoder.geocode({'address': address}, function (results, status) {
          if (status === 'OK') {
            map.setCenter(latLng);
            outer.marker = new window.google.maps.Marker({
              map: map,
              position: latLng
            });
          } else {
            this.snackbarText = status;
            this.snackbarColour = "error";
            this.snackbar = true;
          }
        });
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
      fillInAddress() {
        this.location = this.extractLocationData(this.autocomplete.getPlace());
        this.updateAddressString();
        this.sendLocationToParent();
      },

      /**
       * Extractor function that parses the google maps response and returns a location object.
       */
      extractLocationData(place) {
        let newLocation = {street_address:"",suburb:"",postcode:"",city:"",state:"",country:"",latitude:"",longitude:""};
        let addressComponents = place["address_components"];
        newLocation["latitude"] = place["geometry"]["location"].lat();
        newLocation["longitude"] = place["geometry"]["location"].lng();
        let findingRoute = false;

        for(let i = 0; i < addressComponents.length; i++){
          let content = addressComponents[i]["long_name"];
          if(addressComponents[i]["types"].includes("street_number")){newLocation.street_address = content+" ";findingRoute = true;}
          if(addressComponents[i]["types"].includes("route")){
            if(findingRoute){newLocation.street_address += content}else{newLocation.street_address = content}
          }
          if(addressComponents[i]["types"].includes("sublocality")){newLocation.suburb = content}
          if(addressComponents[i]["types"].includes("locality")){newLocation.city = content}
          if(addressComponents[i]["types"].includes("administrative_area_level_1")){newLocation.state = content}
          if(addressComponents[i]["types"].includes("country")){newLocation.country = content}
          if(addressComponents[i]["types"].includes("postal_code")){newLocation.postcode = content}
        }

        return newLocation;
      },

      /**
       * Calls google maps api with lat lng and updates the location object
       */
      getLocationFromLatLng(latlng) {
        let thisInner = this;
        this.geocoder.geocode({'location': latlng}, function (results, status) {
          if (status === 'OK') {
            if(results.length === 0){
              this.snackbarText = "Invalid Location";
              this.snackbarColour = "error";
              this.snackbar = true;
            }else{
              thisInner.location = thisInner.extractLocationData(results[0]);
              thisInner.updateAddressString();
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
       * Updates the address string from the location object
       */
      updateAddressString() {
        this.address = "";
        if(this.location.street_address !== ""){this.address += this.location.street_address}
        if(this.location.suburb !== ""){
          if(this.address !== ""){this.address += ", "}
          this.address += this.location.suburb;
        }
        if(this.location.city !== ""){
          if(this.address !== ""){this.address += ", "}
          this.address += this.location.city;
        }
        if(this.location.state !== ""){
          if(this.address !== ""){this.address += ", "}
          this.address += this.location.state;
        }
        if(this.location.country !== ""){
          if(this.address !== ""){this.address += ", "}
          this.address += this.location.country;
        }
      },

      /**
       * Sends the location to the parent component
       */
      sendLocationToParent() {
        this.$emit('set-location', this.location);
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