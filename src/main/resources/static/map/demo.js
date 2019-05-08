demo = {
    initDocumentationCharts: function () {
        if ($('#dailySalesChart').length != 0 && $('#websiteViewsChart').length != 0) {
            /* ----------==========     Daily Sales Chart initialization For Documentation    ==========---------- */

            dataDailySalesChart = {
                labels: ['M', 'T', 'W', 'T', 'F', 'S', 'S'],
                series: [
                    [12, 17, 7, 17, 23, 18, 38]
                ]
            };

            optionsDailySalesChart = {
                lineSmooth: Chartist.Interpolation.cardinal({
                    tension: 0
                }),
                low: 0,
                high: 50, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
                chartPadding: {
                    top: 0,
                    right: 0,
                    bottom: 0,
                    left: 0
                },
            };

            var dailySalesChart = new Chartist.Line('#dailySalesChart', dataDailySalesChart, optionsDailySalesChart);

            var animationHeaderChart = new Chartist.Line('#websiteViewsChart', dataDailySalesChart, optionsDailySalesChart);
        }
    },

    initGoogleMaps: function () {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function (position) {


                var pos = {
                    lat: position.coords.latitude,
                    lng: position.coords.longitude
                };
                var myLatlng = new google.maps.LatLng(pos.lat, pos.lng);
                var mapOptions = {
                    zoom: 15,//16,
                    center: myLatlng,
                    scrollwheel: true, //we disable de scroll over the map, it is a really annoing when you scroll through page
                    styles: [
                        {
                            "elementType": "geometry",
                            "stylers": [{
                                "color": "#f5f5f5"
                            }]
                        },
                        {
                            "elementType": "labels.icon",
                            "stylers": [{
                                "visibility": "off"
                            }]
                        },
                        {
                            "elementType": "labels.text.fill",
                            "stylers": [{
                                "color": "#616161"
                            }]
                        },
                        {
                            "elementType": "labels.text.stroke",
                            "stylers": [{
                                "color": "#f5f5f5"
                            }]
                        },
                        {
                            "featureType": "administrative.land_parcel",
                            "elementType": "labels.text.fill",
                            "stylers": [{
                                "color": "#bdbdbd"
                            }]
                        },
                        {
                            "featureType": "poi",
                            "elementType": "geometry",
                            "stylers": [{
                                "color": "#eeeeee"
                            }]
                        },
                        {
                            "featureType": "poi",
                            "elementType": "labels.text.fill",
                            "stylers": [{
                                "color": "#757575"
                            }]
                        },
                        {
                            "featureType": "poi.park",
                            "elementType": "geometry",
                            "stylers": [{
                                "color": "#e5e5e5"
                            }]
                        },
                        {
                            "featureType": "poi.park",
                            "elementType": "labels.text.fill",
                            "stylers": [{
                                "color": "#9e9e9e"
                            }]
                        },
                        {
                            "featureType": "road",
                            "elementType": "geometry",
                            "stylers": [{
                                "color": "#ffffff"
                            }]
                        },
                        {
                            "featureType": "road.arterial",
                            "elementType": "labels.text.fill",
                            "stylers": [{
                                "color": "#757575"
                            }]
                        },
                        {
                            "featureType": "road.highway",
                            "elementType": "geometry",
                            "stylers": [{
                                "color": "#dadada"
                            }]
                        },
                        {
                            "featureType": "road.highway",
                            "elementType": "labels.text.fill",
                            "stylers": [{
                                "color": "#616161"
                            }]
                        },
                        {
                            "featureType": "road.local",
                            "elementType": "labels.text.fill",
                            "stylers": [{
                                "color": "#9e9e9e"
                            }]
                        },
                        {
                            "featureType": "transit.line",
                            "elementType": "geometry",
                            "stylers": [{
                                "color": "#e5e5e5"
                            }]
                        },
                        {
                            "featureType": "transit.station",
                            "elementType": "geometry",
                            "stylers": [{
                                "color": "#eeeeee"
                            }]
                        },
                        {
                            "featureType": "water",
                            "elementType": "geometry",
                            "stylers": [{
                                "color": "#c9c9c9"
                            }]
                        },
                        {
                            "featureType": "water",
                            "elementType": "labels.text.fill",
                            "stylers": [{
                                "color": "#9e9e9e"
                            }]
                        }
                    ]

                };
                const map = new google.maps.Map(document.getElementById("map"), mapOptions);

                var createChallengeWindow = new google.maps.InfoWindow;

                var mousedUp = false;
                map.addListener('mousedown', function (event) {
                    mousedUp = false;
                    setTimeout(function(){
                        if(mousedUp === false){
                    const latitude = event.latLng.lat();
                    const longitude = event.latLng.lng();
                    var a = document.createElement('a');
                    var linkText = document.createTextNode(createChallenge);
                    a.appendChild(linkText);
                    a.style.color = "black";
                    a.href = "/challenge/create?latCoords=" + latitude + "&lngCoords=" + longitude;
                    document.body.appendChild(a);
                    // const createChallengeLink = "<b><a href='/challenge/create?latCoords='  style='color:black'>VYTVOŘIT VÝZVU</a></b>";
                    createChallengeWindow.setContent(a);
                    createChallengeWindow.setPosition({lat: latitude, lng: longitude});
                    createChallengeWindow.open(map);
                        }
                    }, 500);
                });
                map.addListener('mouseup', function(event){
                    mousedUp = true;
                });
                map.addListener('dragstart', function(event){
                    mousedUp = true;
                });
                const avatarIcon = {
                    url: "/img/avatars/Default.png", // url
                    scaledSize: new google.maps.Size(50, 50), // scaled size
                    origin: new google.maps.Point(0, 0), // origin
                    anchor: new google.maps.Point(0, 0) // anchor
                };

                const avatarMarker = new google.maps.Marker({
                    position: new google.maps.LatLng(pos.lat, pos.lng), // position
                    map: map, // same as avatarMarker.setMap(map);
                    icon: avatarIcon, // icon of marker
                    title: "Zde se nacházíš!"
                });

                //marker.setMap(map);


                // To add the marker to the map, call setMap();


                var infowindow = new google.maps.InfoWindow;

                // jednotlivy vyzvy
                for (let i = 0; i < challenges.length; i++) {
                    //https://stackoverflow.com/questions/25638834/mutable-variable-is-accessible-from-closure/25638959
                    // Zobrazovala to markery jen jak se tomu zachtělo bez tejhle obalovací fce
                    (function(){
                    var icon = {
                         url: "/img/activities/" + challenges[i][10] + ".png",//+ challenges[i][9] + ".png", // url
                        scaledSize: new google.maps.Size(50, 50), // scaled size
                        origin: new google.maps.Point(0, 0), // origin
                        anchor: new google.maps.Point(0, 0) // anchor
                    };

                    var marker = new google.maps.Marker({
                        position: new google.maps.LatLng(challenges[i][0], challenges[i][1]),
                        map: map,
                        icon: icon
                    });

                    var defaultIcon = {
                        url: "/img/activities/Default.png",
                        scaledSize: new google.maps.Size(50, 50), // scaled size
                        origin: new google.maps.Point(0, 0), // origin
                        anchor: new google.maps.Point(0, 0) // anchor
                    };

                    var ic = new Image();
                    ic.src = "/img/activities/" + challenges[i][10] + ".png";//+ challenges[i][9] + ".png";

                    ic.onload = function () {
                        marker.setIcon(icon); //If icon found go ahead and show it
                    };
                   // var blabla = "ahoj"
                    ic.onerror = function () {
                        //https://stackoverflow.com/questions/25058443/google-maps-api-v-3-use-default-marker-icon-if-not-found-at-url
                        marker.setIcon(defaultIcon); //This displays brick colored standard marker icon in case image is not found.
                    };

                    // Vyskakovaci okno vyzvy(markeru), kdyz na ni klikneme.
                    google.maps.event.addListener(marker, 'click', (function (marker, i) {
                        return function () {
                            const linkToChallenge = "linkToChallenge";//<b><a href='/user/challengeDetail?challengeId=" + challenges[i][8] + "'><spring:message code="map.linkToChallenge"/></a></b>";
                            infowindow.setContent(
                                challenges[i][2] + "<br/>" +
                                challenges[i][3] + "<br/>" +
                                challenges[i][4] + "<br/>" +
                                challenges[i][5] + "<br/>" +
                                challenges[i][6] + "<br/>" +
                                challenges[i][7] + "<br/>" +
                                challenges[i][11] + "<br/>" + "<br/>" +
                                '<a style="color:black" href=/challenge/detail?challengeId=' + challenges[i][9] + '>' + challenges[i][8] + '</a>'
                            );
                            infowindow.open(map, marker);
                        }
                    })(marker, i));
                    })();
                }

                map.addListener('rightClick', function () {
                    map.setZoom(8);
                });
            });
        }
    }
};