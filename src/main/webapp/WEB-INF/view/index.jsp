<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <%@ include file="meta.jsp" %>
        <title>SkyCast Inc. | Weather Forecast App</title>

        <%@ include file="bootstrap-scripts.jsp"%>
        <script src="https://code.highcharts.com/highcharts.src.js"></script>
        <link rel="stylesheet" href="resources/css/index.css" />
    </head>

    <body>
        <div class="container-fluid" style="background: #ceddf5; padding-bottom: 40px;">
            <div class="row">
                <div class="col">
                    <h1 class="text-center" style="color: #000;">SkyCast Inc.</h1>
                </div>
            </div>
            <!-- Weather Forecast Search -->
            <div class="row">
                <div class="col">
                    <form action="/getForecast" method="get">
                        <div class="form-group">
                            <div class="col-xs-6 col-xs-offset-3 text-center">
                                <input type="text" name="location" class="form-control location-input" id="location"
                                       placeholder="Enter a location" value="${location}" required />
                                <button type="submit" class="btn btn-info" id="locationSearch" style="vertical-align: top;">
                                    <i class="fa fa-search" aria-hidden="true"></i> Search
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="row" id="cacheRow">
                <div class="form-group">
                    <div class="col-xs-6 col-xs-offset-3 text-center" style="margin-top: 10px">
                        <select class="form-control location-input" id="cachedLocations">
                            <option value="" disabled selected>Previous Locations Searched:</option>
                        </select>
                        <button class="btn btn-info" style="visibility: hidden;"><i class="fa fa-search" aria-hidden="true"></i> Search</button>
                    </div>
                </div>
            </div>
        </div>
        <br />
        <!-- Forecast Results -->
        <div style="${isDataRetrieved ? '' : 'display: none;'}">
            <!-- Current Forecast -->
            <div class="container-fluid" >
                <div class="row">
                    <div class="col text-center">
                        <h2>Today's Forecast</h2><br />
                    </div>
                </div>
                <div class="row" style="height: 50px;">
                    <div class="col text-center">
                        <canvas id="icon" width="50px" height="50px"></canvas>&nbsp;&nbsp;
                        <span style="font-weight: bold; font-size: 32px; vertical-align: top;">${temperature}&deg;F ${summary}</span>
                    </div>
                </div>
            </div>
            <br />
            <hr />
            <!-- Future Forecast -->
            <div class="container-fluid">
                <div class="row">
                    <div class="col text-center">
                        <h2>Next ${numOfDaysToForecast} Days' Forecasts</h2><br />
                    </div>
                </div>
                <div class="row">
                    <c:forEach items="${futureDayOfWeeks}" var="dayOfWeek" varStatus="dayNum">
                        <div class="text-center col-sm-2<c:if test="${dayNum.index eq 0}"> col-sm-offset-3</c:if>">
                            <div class="col forecast-window">
                                <p>High ${futureTempHighs[dayNum.index]}&deg;F</p>
                                <p>Low ${futureTempLows[dayNum.index]}&deg;F</p>
                                <p>${dayOfWeek}</p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
            <br /><br />
            <hr />
            <!-- Historical Weather Data -->
            <div class="container-fluid">
                <div class="row">
                    <div class="col text-center">
                        <h2>Historical Weather Data For This Location</h2><br />
                    </div>
                </div>
                <div class="row">
                    <div class="col text-center">
                        <div id="container"></div>
                    </div>
                </div>
            </div>
        </div>
        <br /><br />
        <!-- Footer -->
        <div class="row footer">
            <div class="col text-center" style="background: #fff;">
                <a href="https://darksky.net/poweredby/" style="margin-bottom: 2px;">Powered by Dark Sky</a>
            </div>
        </div>

        <script src="resources/js/skycons.js"></script>
        <script type="text/javascript">
            var skycons = new Skycons({"color": "black"});
            skycons.add("icon", "${icon}");

            // Start animation
            skycons.play();
        </script>
        <script type="text/javascript">
            Highcharts.chart('container', {

                title: {
                    text: 'High & Low Temperatures Over the Past Week'
                },

                xAxis: {
                    title: {
                        text: 'Date'
                    },
                    type: 'datetime'
                },

                yAxis: {
                    title: {
                        text: 'Temperature'
                    }
                },

                plotOptions: {
                    series: {
                        // Multiply by 1000 to account for differences between Unix and JS time
                        pointStart: ${startingUnixTime * 1000},
                        pointInterval: 24 * 3600 * 1000 // 1 day
                    }
                },

                series: [{
                    name: 'High Temperatures',
                    data: ${pastTempHighs},
                    color: '#ff4450'
                }, {
                    name: 'Low Temperatures',
                    data: ${pastTempLows},
                    color: '#3899ff'
                }],

                responsive: {
                    rules: [{
                        condition: {
                            maxWidth: 500
                        }
                    }]
                }

            });
        </script>

        <!-- Caching Script -->
        <script type="text/javascript">
            //localStorage.clear();

            var cacheAdder = function() {
                if (typeof(Storage) !== "undefined") { // Check browser support
                    localStorage.setItem($("#location").val(), "");
                } else {
                    // User's browser doesn't support Web Storage
                }
            };

            $("#locationSearch").on("click", cacheAdder);

            var cachedLocations = $("#cachedLocations");
            var locationInput = $("#location");

            (function retrieveCache() {
                if (typeof(Storage) !== "undefined") {
                    Object.keys(localStorage).forEach(function (key, index) {
                        cachedLocations.append($('<option>', {
                            value: key,
                            text : key
                        }));
                    });
                }
            })();

            // Load a cached value into search bar
            cachedLocations.change(function() {
                var selectedLocation = cachedLocations.find(":selected").text();
                locationInput.val(selectedLocation);
            });

            // Only show previous searches elements if cache isn't empty
            (function isCacheEmpty() {
                if (typeof(Storage) !== "undefined") {
                    if (Object.keys(localStorage).length > 0) {
                        $("#cacheRow").show();
                    } else {
                        $("#cacheRow").hide();
                    }
                } else {
                    $("#cacheRow").hide();
                }
            })();
        </script>
    </body>
</html>