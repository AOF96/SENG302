<template>
    <div class="form-popup" id="myForm">
        <form action="/action_page.php" class="form-container">
            <h1>New Activity</h1>

            <label for="name"><b>Activity Name</b></label>
            <input type="text" id="name" v-model="activity.name" required>

            <label for="time">Continuous?</label>
            <select id="time" v-on:change="setDuration" v-model="duration">
                <option value="continuous">Continuous</option>
                <option value="duration">Duration</option>
            </select>

            <label class="activityDurationLabel" for="start_date">Start Date</label>
            <input type = "date" id="start_date" v-model="start_date">

            <label class="activityDurationLabel" for="end_date">End Date</label>
            <input type = "date" id="end_date" v-model="end_date">

            <label class="activityDurationLabel" for="start_time">Start Time</label>
            <input type = "time" id="start_time" v-model="start_time">

            <label class="activityDurationLabel" for="end_time">End Time</label>
            <input type = "time" id="end_time" v-model="end_time">

            <label for="desc"><b>Description</b></label>
            <input type="text" id="desc" v-model="activity.description">

            <label><b>Location</b></label>
            <div>
                <select v-model="adding_country"
                        name="countries"
                        placeholder="Countries"
                        value="Countries"
                        required>
                    <option selected disabled hidden>Countries</option>
                    <option v-for="addingCountry in countries_option" v-bind:key="addingCountry">
                        {{addingCountry}}
                    </option>
                </select>
            </div>

            <label><b>Activity Types</b></label>
            <div>
                <select
                        v-on:change="selectActivityType"
                        v-model="selected_activity"
                        name="activityType"
                        placeholder="Activity Type"
                        value="Activity Type"
                        required
                >
                    <option selected disabled hidden>Activity Type</option>
                    <option v-for="addingActivity in activities_option" v-bind:key="addingActivity">
                        {{addingActivity}}
                    </option>
                </select>
            </div>
            <div v-for="addedActivity in activity_types_selected" v-bind:key="addedActivity">
                <h5>{{addedActivity}}</h5>
                <button v-on:click="removeActivityType(addedActivity)">Remove</button>
            </div>

            <button type="submit" class="btn" v-on:click="addActivity">Create</button>
        </form>
    </div>
</template>

<script>
    import {mapGetters, mapActions} from 'vuex'
    import {apiUser, apiActivity} from "../api";
    import axios from "axios";
    import router from "../router";
    const COUNTRIES_URL = 'https://restcountries.eu/rest/v2/all'

    export default {
        data() {
            return {
                selected_activity: "Activity Type",
                activities_option: [],
                countries_option: [],
                adding_country: "Countries",
                duration: "duration",
                activity_types_selected: [],
                start_date: null,
                end_date: null,
                start_time: null,
                end_time: null
            }
        },
        computed: {
            ...mapGetters(['activity']),
            ...mapGetters(['user'])
        },
        created: async function() {
            // Ensures only activity types from the database can be selected and cannot select ones already selected
            await apiUser.getActivityTypes().then((response) => {
                this.activities_option = response.data;
            }).catch(error => console.log(error));
            await axios.get(COUNTRIES_URL).then((response) => {
                const countries = [];
                const data = response.data;
                for (let country in data) {
                    let country_name = data[country].name;
                    countries.push(country_name)
                }
                this.countries_option = countries
            })
            .catch(error => console.log(error))
        },
        methods: {
            ...mapActions(['createActivity']),

            setDuration() {
                let i;
                if (this.duration === "duration") {
                    document.getElementById("start_date").hidden = false;
                    document.getElementById("end_date").hidden = false;
                    document.getElementById("start_time").hidden = false;
                    document.getElementById("end_time").hidden = false;
                    let labels = document.getElementsByClassName("activityDurationLabel");
                    for(i = 0; i < labels.length; i++) {
                        labels[i].hidden = false;
                    }
                } else {
                    document.getElementById("start_date").hidden = true;
                    document.getElementById("end_date").hidden = true;
                    document.getElementById("start_time").hidden = true;
                    document.getElementById("end_time").hidden = true;
                    let labels = document.getElementsByClassName("activityDurationLabel");
                    for(i = 0; i < labels.length; i++) {
                        labels[i].hidden = true;
                    }
                }
            },
            selectActivityType() {
                if (this.selected_activity !== undefined) {
                    this.activity_types_selected.push(this.selected_activity);
                    let index = this.activities_option.indexOf(this.selected_activity);
                    if (index !== -1) {
                        this.activities_option.splice(index, 1);
                    }
                }
            },
            removeActivityType(addedActivity) {
                this.activities_option.push(addedActivity);
                let index = this.activity_types_selected.indexOf(addedActivity);
                if (index !== -1) {
                    this.activity_types_selected.splice(index, 1);
                }
            },
            addActivity() {
                let combinedStartTime = this.start_date + 'T' + this.start_time + ':00+1300';
                let combinedEndTime = this.end_date + 'T' + this.end_time + ':00+1300';
                console.log(combinedEndTime);

                this.duration = this.duration !== 'duration';

                apiActivity.addActivity(this.user.profile_id, this.activity.name, this.duration, combinedStartTime,
                    combinedEndTime, this.activity.description, this.adding_country, this.activity_types_selected)
                    .then(response => {
                        console.log(response);
                            router.push("Profile");
                        },
                        error => {
                            console.log(error);
                        }
                    );
            }
        }
    }
</script>