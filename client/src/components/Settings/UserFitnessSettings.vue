<template>
    <div id="settingsWrap">
        <UserSettingsMenu />
        <div class="settingsContent">
            <h1>Fitness Level</h1>
            <hr>
            <h3>Current Fitness Level</h3>
            <div class="emailBlock">
                <h4>{{fitnesslevel}}</h4>
        </div>
            <h3>Change Fitness Level:</h3>
            <div class="signup-row">
                <select id="levels" v-model="fitnesslevel">
                    <option >0</option>
                    <option >1</option>
                    <option >2</option>
                    <option >3</option>
                    <option >4</option>
                    <option >5</option>
                </select>
            </div>
            <button id="updateFitnessButton" v-on:click="updateFitnessLevel()">Update</button>
    </div>
    </div>
</template>
<script>
    import  UserSettingsMenu from '@/components/Settings/UserSettingsMenu'
   import {
       userInfo
   } from "../../globals";
    export default {
        components: {
            UserSettingsMenu
        },
        data() {
            return {
                fitnesslevel: userInfo.fitnesslevel,
            }
        },
        methods: {
            updateFitnessLevel(){
                if(this.fitnesslevel != ""){
                userInfo.fitnesslevel = this.fitnesslevel;
                console.log(userInfo.fitnesslevel);
                alert("Fitness level updated.");
            }}
        }
    }
</script>
<style scoped>
</style>