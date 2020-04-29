<template>
    <div class="activityInfo">
        <div class="activityTitle">{{ activityInfo.name }}</div>
        <div class="activityDescription">{{ activityInfo.description }}</div>
        <div class="activityLocation">{{ activityInfo.location }}</div>
        <div class="activityStart"><h3> Start date: {{ activityInfo.start }}</h3></div>
        <div class="activityEnd"><h3> End date: {{ activityInfo.end }}</h3></div>
        <div class="activityDuration">{{ activityInfo.duration }}</div>
        <div class="activityTypeTitle"><p>Activity Type: </p></div>
        <div class="activityTypes">
            <span v-for="a in activityInfo.activityTypes" :key="a.type_id">
                <span v-if="activityInfo.activityTypes.indexOf(a) != activityInfo.activityTypes.length - 1">
                    {{a.name}} ,
                </span>
                <span v-else>
                    {{a.name}}.
                </span>
            </span>
        </div>


    </div>
</template>

<script>
  export default {
    name: "ActivityPageInfo",
    props: ["activityInfo"]
  }
</script>

<style scoped>
    .activityInfo {
        font-family: sans-serif;
        margin: 0;
        font-family: 'Roboto';
        display: grid;
        grid-template-columns: auto 30%;
        grid-template-rows: 2fr 2fr 1fr 1fr 2fr;
        grid-template-areas: "Title Title" "Description Location" "Description Start" "ActivityTypesTitle End" "ActivityTypes Duration";
    }

    .activityTitle {
        padding-left: 1rem;
        padding-top: 0.5rem;
        padding-bottom: 0.5rem;
        display: flex;
        justify-content: center;
        align-items: center;
        text-align: center;
        color: #1cca92;
        font-size: 22px;
        grid-area: Title;
        font-weight: 500;

    }

    .activityDescription {
        display: flex;
        grid-area: Description;
        align-items: center;
        padding-bottom: 2rem;
        padding-left: 2rem;
        padding-right: 0.5rem;
        font-size: 17px;

    }

    .activityLocation {
        display: flex;
        justify-content: center;
        align-items: center;
        text-align: center;
        grid-area: Location;
        color: #6b6b6b;
        font-size: 17px;

    }

    .activityStart {
        display: flex;
        justify-content: center;
        align-items: flex-end;
        text-align: center;
        grid-area: Start;
        font-size: 12px;
        color: #6b6b6b;

    }

    .activityEnd {
        text-align: center;
        grid-area: End;
        font-size: 12px;
        color: #6b6b6b;

    }


    .activityDuration {
        display: flex;
        justify-content: center;
        align-items: center;
        text-align: center;
        grid-area: Duration;
        font-size: 15px;
        color: #6b6b6b;


    }

    .activityTypeTitle{
        grid-area: ActivityTypesTitle;
        font-size: 15px;
        display: flex;
        justify-content: center;
        align-items: flex-end;
        text-align: center;
        font-weight: 500;
        colour: black;


    }

    .activityTypes {
        grid-area: ActivityTypes;
        text-align: center;
        font-size: 15px;
        color: #1cca92;
        font-weight: 500;

    }




</style>