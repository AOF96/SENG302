<template>
  <div id="map">
    <v-snackbar outlined color="error" :timeout="timeout" v-model="snackbar" bottom>{{errorMessage}}</v-snackbar>
  </div>
</template>

<script>
  import {mapGetters, mapState, mapActions} from "vuex";

  export default {
    name: "Map",
    data: function () {
      return {
        map: null,
        geocoder: null,
        errorMessage: null,
        snackbar: false,
        timeout: 2000,
        activities: [
          {
            id: 1,
            name: "Walking away",
            visibility: "public",
            latitude: -34.0,
            longitude: 150,
            author: 86007
          },
          {
            id: 2,
            name: "Walking towards",
            visibility: "public",
            latitude: -35.0,
            longitude: 151,
            author: 86009
          },
          {
            id: 3,
            name: "Walking away",
            visibility: "restricted",
            latitude: -34.2,
            longitude: 150.2,
            author: 86007
          },
          {
            id: 4,
            name: "Walking towards",
            visibility: "restricted",
            latitude: -35.2,
            longitude: 151.2,
            author: 86009
          },
          {
            id: 5,
            name: "Walking away",
            visibility: "private",
            latitude: -34.4,
            longitude: 150.4,
            author: 86007
          },
          {
            id: 6,
            name: "Walking towards",
            visibility: "private",
            latitude: -35.4,
            longitude: 151.4,
            author: 86009
          }
        ]
      }
    },
    computed: {
      ...mapState(["user"]),
      ...mapGetters(["user"])
    },
    mounted() {
      this.loadMap();
    },
    methods: {
      ...mapActions(["getDataFromUrl"]),
      /**
       * Loads the map onto the page and centres on the users home city.
       * Adds a marker on the city's centre.
       */
      loadMap() {
        this.geocoder = new window.google.maps.Geocoder();

        let position = new window.google.maps.LatLng(this.user.location.latitude, this.user.location.longitude);

        const styles = {
          light: [
            {
              featureType: "poi",
              stylers: [
                {
                  visibility: "off"
                }
              ]
            },
          ],
          dark: [
            {
              featureType: "poi",
              stylers: [
                {
                  visibility: "off"
                }
              ]
            },
            {elementType: 'geometry', stylers: [{color: '#242f3e'}]},
            {elementType: 'labels.text.stroke', stylers: [{color: '#242f3e'}]},
            {elementType: 'labels.text.fill', stylers: [{color: '#746855'}]},
            {
              featureType: 'road',
              elementType: 'geometry',
              stylers: [{color: '#38414e'}]
            },
            {
              featureType: 'road',
              elementType: 'geometry.stroke',
              stylers: [{color: '#212a37'}]
            },
            {
              featureType: 'road',
              elementType: 'labels.text.fill',
              stylers: [{color: '#9ca5b3'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'geometry',
              stylers: [{color: '#746855'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'geometry.stroke',
              stylers: [{color: '#1f2835'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'labels.text.fill',
              stylers: [{color: '#f3d19c'}]
            },
            {
              featureType: 'transit',
              elementType: 'geometry',
              stylers: [{color: '#2f3948'}]
            },
            {
              featureType: 'transit.station',
              elementType: 'labels.text.fill',
              stylers: [{color: '#d59563'}]
            },
            {
              featureType: 'water',
              elementType: 'geometry',
              stylers: [{color: '#17263c'}]
            },
            {
              featureType: 'water',
              elementType: 'labels.text.fill',
              stylers: [{color: '#515c6d'}]
            },
            {
              featureType: 'water',
              elementType: 'labels.text.stroke',
              stylers: [{color: '#17263c'}]
            }
          ]
        };

        let map = new window.google.maps.Map(document.getElementById("map"), {
          center: position,
          zoom: 12,
          styles: styles["light"]
        });

        this.createHomeMarker(map, position);
        this.createActivityMarkers(map);
      },

      /**
       * Creates a blue marker at the users home location
       * @param map
       * @param position
       */
      createHomeMarker(map, position) {
        var homeIcon = {
          url: "https://i.imgur.com/mNfVgmC.png",
          scaledSize: new window.google.maps.Size(20, 20),
          origin: new window.google.maps.Point(0, 0),
          anchor: new window.google.maps.Point(10, 10)
        };

        let marker = new window.google.maps.Marker({
          map: map,
          position: position,
          icon: homeIcon
        });

        let contentString = '<div id="content">'+
            '<h2>Your Location</h2>'+
            '<div id="bodyContent">'+
            this.locationToString(this.user.location) +
            '</div>'+
            '</div>';

        let infowindow = new window.google.maps.InfoWindow({
          content: contentString
        });

        marker.addListener('click', function() {
          infowindow.open(map, marker);
        });

        window.google.maps.event.addListener(map, "click", function() {
          infowindow.close();
        });
      },

      /**
       * Creates a green marker for public activities
       * @param map
       */
      createActivityMarkers(map) {
        var activityMarkerIcon = {
          url: "https://i.imgur.com/MUWKzz9.png",
          scaledSize: new window.google.maps.Size(30, 30),
          origin: new window.google.maps.Point(0, 0),
          anchor: new window.google.maps.Point(10, 10)
        };

        for (let activity of this.activities) {
          if (activity.visibility === "public") {
            if(activity.author === this.user.profile_id) {
              activityMarkerIcon.url = "https://i.imgur.com/Hz5QgGa.png"
            } else {
              activityMarkerIcon.url = "https://i.imgur.com/MUWKzz9.png"
            }
          } else if (activity.visibility === "restricted") {
            if(activity.author === this.user.profile_id) {
              activityMarkerIcon.url = "https://i.imgur.com/61rB4dm.png"
            } else {
              activityMarkerIcon.url = "https://i.imgur.com/Y0JUUox.png"
            }
          } else if (activity.visibility === "private") {
            if(activity.author === this.user.profile_id) {
              activityMarkerIcon.url = "https://i.imgur.com/jNY9HSw.png"
            } else {
              activityMarkerIcon.url = "https://i.imgur.com/lanhJgs.png"
            }
          }

          let activityPosition = new window.google.maps.LatLng(activity.latitude, activity.longitude);
          new window.google.maps.Marker({
            map: map,
            position: activityPosition,
            icon: activityMarkerIcon,
          });
        }
      },

      /**
       * Converts given location object to a formatted string
       * @param location
       * @returns {string}
       */
      locationToString(location) {
        let city = location.city;
        let state = location.state;
        let country = location.country;
        let streetAddress = location.street_address;
        let outputString = "";

        if (location === {}) {
          outputString = "No Location Set"
        } else {
          outputString += streetAddress;
          if(city !== ""){
            if(outputString !== ""){outputString += ", "}
            outputString += city;
          }
          if(state !== ""){
            if(outputString !== ""){outputString += ", "}
            outputString += state;
          }
          if(country !== ""){
            if(outputString !== ""){outputString += ", "}
            outputString += country;
          }
          if(outputString === ""){
            outputString = "No Location Set"
          }
        }
        return outputString;
      },

      /**
       * Navigates to the users profile
       */
      goToProfile() {
        this.$router.push('/profile/' + this.user.profile_id)
      }
    }
  }
</script>

<style scoped>
  @import "../../../public/styles/pages/mapStyle.css";
</style>