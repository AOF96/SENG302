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
        mapActivities: [],
        activities: [
          {
            id: 1,
            name: "Walking away",
            continuous: true,
            description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec libero magna, vulputate vitae molestie quis, vulputate a justo.",
            followers: 4,
            activityTypes: "Team sport, Running",
            visibility: "public",
            start_time: "",
            end_time: "",
            author: 86007,
            location: {
              street_address: "Avon River",
              city: "Christchurch",
              country: "New Zealand",
              latitude: -43.1213,
              longitude: 172.614962,
            }
          },
          {
            id: 2,
            name: "Walking towards",
            visibility: "public",
            description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec libero magna, vulputate vitae molestie quis, vulputate a justo.",
            latitude: -35.0,
            longitude: 151,
            author: 86009,
            followers: 4,
            activityTypes: "Team sport, Running",
            location: {
              street_address: "Avon River",
              state: "",
              city: "Christchurch",
              country: "New Zealand",
              latitude: -43.879534,
              longitude: 172.614962,
            },
            continuous: true,
            start_time: "",
            end_time: ""
          },
          {
            id: 3,
            name: "Walking away",
            visibility: "restricted",
            description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec libero magna, vulputate vitae molestie quis, vulputate a justo.",
            latitude: -34.2,
            longitude: 150.2,
            author: 86007,
            followers: 4,
            activityTypes: "Team sport, Running",
            location: {
              street_address: "Avon River",
              city: "Christchurch",
              state: "",
              country: "New Zealand",
              latitude: -43.4568,
              longitude: 172.614962,
            },
            continuous: true,
            start_time: "",
            end_time: ""
          },
          {
            id: 4,
            name: "Walking towards",
            visibility: "restricted",
            description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec libero magna, vulputate vitae molestie quis, vulputate a justo.",
            latitude: -35.2,
            longitude: 151.2,
            author: 86009,
            followers: 4,
            activityTypes: "Team sport, Running",
            location: {
              street_address: "Avon River",
              city: "Christchurch",
              state: "",
              country: "New Zealand",
              latitude: -43.23423,
              longitude: 172.614962,
            },
            continuous: true,
            start_time: "",
            end_time: ""
          },
          {
            id: 5,
            name: "Walking away",
            visibility: "private",
            description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec libero magna, vulputate vitae molestie quis, vulputate a justo.",
            latitude: -34.4,
            longitude: 150.4,
            author: 86007,
            followers: 4,
            activityTypes: "Team sport, Running",
            location: {
              street_address: "Avon River",
              city: "Christchurch",
              state: "",
              country: "New Zealand",
              latitude: -43.234231,
              longitude: 172.614962,
            },
            continuous: true,
            start_time: "",
            end_time: ""
          },
          {
            id: 6,
            name: "Walking towards",
            visibility: "private",
            description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec libero magna, vulputate vitae molestie quis, vulputate a justo.",
            latitude: -35.4,
            longitude: 151.4,
            author: 86009,
            followers: 4,
            activityTypes: "Team sport, Running",
            location: {
              street_address: "Avon River",
              city: "Christchurch",
              state: "",
              country: "New Zealand",
              latitude: -43.23423523,
              longitude: 172.614962,
            },
            continuous: true,
            start_time: "",
            end_time: ""
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
              "featureType": "administrative",
              "elementType": "geometry",
              "stylers": [
                {
                  "visibility": "off"
                }
              ]
            },
            {
              "featureType": "administrative",
              "elementType": "labels.text.fill",
              "stylers": [
                {
                  "color": "#454545"
                }
              ]
            },
            {
              "featureType": "landscape.man_made",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#eaf0ed"
                }
              ]
            },
            {
              "featureType": "landscape.natural",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#b0eecb"
                }
              ]
            },
            {
              "featureType": "landscape.natural",
              "elementType": "labels.icon",
              "stylers": [
                {
                  "visibility": "off"
                }
              ]
            },
            {
              "featureType": "landscape.natural.landcover",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#33ffad"
                },
                {
                  "visibility": "simplified"
                }
              ]
            },
            {
              "featureType": "landscape.natural.terrain",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#9ae4be"
                },
                {
                  "weight": 1
                }
              ]
            },
            {
              "featureType": "poi",
              "stylers": [
                {
                  "visibility": "off"
                }
              ]
            },
            {
              "featureType": "poi",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "visibility": "off"
                }
              ]
            },
            {
              "featureType": "poi.attraction",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#d7e5de"
                },
                {
                  "visibility": "on"
                }
              ]
            },
            {
              "featureType": "poi.business",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#cee3dd"
                },
                {
                  "visibility": "on"
                }
              ]
            },
            {
              "featureType": "poi.government",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#d7e5de"
                },
                {
                  "visibility": "on"
                }
              ]
            },
            {
              "featureType": "poi.medical",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#c7dbd8"
                },
                {
                  "visibility": "on"
                }
              ]
            },
            {
              "featureType": "poi.park",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#8fdbba"
                },
                {
                  "visibility": "on"
                }
              ]
            },
            {
              "featureType": "poi.school",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#d0e2de"
                },
                {
                  "visibility": "on"
                }
              ]
            },
            {
              "featureType": "poi.sports_complex",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#84d2b4"
                },
                {
                  "visibility": "on"
                }
              ]
            },
            {
              "featureType": "road",
              "elementType": "labels.icon",
              "stylers": [
                {
                  "visibility": "off"
                }
              ]
            },
            {
              "featureType": "road.arterial",
              "elementType": "labels",
              "stylers": [
                {
                  "visibility": "off"
                }
              ]
            },
            {
              "featureType": "road.highway",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#ffffff"
                }
              ]
            },
            {
              "featureType": "road.highway",
              "elementType": "geometry.stroke",
              "stylers": [
                {
                  "visibility": "simplified"
                }
              ]
            },
            {
              "featureType": "road.highway",
              "elementType": "labels.text.fill",
              "stylers": [
                {
                  "color": "#45696e"
                }
              ]
            },
            {
              "featureType": "road.local",
              "stylers": [
                {
                  "visibility": "off"
                }
              ]
            },
            {
              "featureType": "road.local",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "visibility": "on"
                }
              ]
            },
            {
              "featureType": "transit",
              "stylers": [
                {
                  "visibility": "off"
                }
              ]
            },
            {
              "featureType": "transit.line",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#9cbaae"
                },
                {
                  "visibility": "on"
                }
              ]
            },
            {
              "featureType": "transit.station",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#9cbaae"
                },
                {
                  "visibility": "off"
                }
              ]
            },
            {
              "featureType": "transit.station.airport",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#e9f2ef"
                },
                {
                  "visibility": "on"
                }
              ]
            },
            {
              "featureType": "water",
              "elementType": "geometry.fill",
              "stylers": [
                {
                  "color": "#7abbdc"
                }
              ]
            }
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
        let innerThis = this;
        var activityMarkerIcon = {
          url: "https://i.imgur.com/MUWKzz9.png",
          scaledSize: new window.google.maps.Size(30, 30),
          origin: new window.google.maps.Point(0, 0),
          anchor: new window.google.maps.Point(15, 30)
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

          let activityPosition = new window.google.maps.LatLng(activity.location.latitude, activity.location.longitude);
          let pos = this.mapActivities.length;
          innerThis.mapActivities.push(new window.google.maps.Marker({
            map: map,
            position: activityPosition,
            icon: activityMarkerIcon,
          }));

          let contentString = '<div id="content">'+
              '<div id="activityPopupActivityVisibility">'+ activity.visibility+ '</div>'+
              '<div id="activityPopupLocation">'+ this.locationToString(activity.location) + '</div>'+
              '<h2 class="activityPopupTitle">'+ activity.name +'</h2>'+
              '<div id="activityPopupDescription">'+ activity.description + '</div>'+
              '<div id="activityPopupActivityTypes">'+ activity.activityTypes + '</div>'+
              '<div id="activityPopupActivityFollowers">'+ activity.followers + ' followers</div>'+
              '</div>';

          let infowindow = new window.google.maps.InfoWindow({
            content: contentString
          });

          innerThis.mapActivities[pos].addListener('click', function() {
            infowindow.open(map, innerThis.mapActivities[pos]);
          });

          window.google.maps.event.addListener(map, "click", function() {
            infowindow.close();
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