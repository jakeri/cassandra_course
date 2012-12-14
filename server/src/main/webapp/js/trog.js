/**

  This library makes two back end requests:

  ../meta
  Takes no parameters.
  The expected return value is a two lists. The first one contains all host names, the second one contains all metric names.

  ../stat
  Takes two parameters:
    * host, which is the name of a host to fetch data for, e.g. localhost
    * metric, which is a metric to fetch, e.g. cpu
  The expected return value is a list of data entries, where each entry is a list containing two elements, a timestamp and a value. The entries should be sorted by time.

*/

var trog = {
	init: function() {

		Highcharts.setOptions({
			global: {
				useUTC: false
			}
		});

        trog.chart = new Highcharts.StockChart({

            chart : {
                renderTo: 'chart',
				animation: false,
            },

            rangeSelector : {
				buttons:[]
            },

            title : {
                text : "Host Monitor"
            },

            series : [{
                name : "data",
                data : [],
                tooltip: {
                    valueDecimals: 2
                }
            }]
		});
		$("select").change(trog.fetch);
		$.ajax({
			url: "./meta/",
			dataType: 'json',
			success: trog.handleMetadata
		});
	},

	handleMetadata: function(meta) {
		console.log(meta);
		_.map(meta[0], function(v) {$('#host').append($("<option>").text(v).attr("value", v));});
		_.map(meta[1], function(v) {$('#metric').append($("<option>").text(v).attr("value", v));});
		trog.fetch();
	},

	fetch: function() {
		console.log("fetch");
		var m = $('#metric :selected')[0].value;
		var host = $('#host :selected')[0].value;
		var url = "./stat/";

		var data = {
			'metric': m, 'host': host
		};
		$.ajax({
			url: url,
			dataType: 'json',
			data: data,
			success: function(data) { console.log(data);trog.plot(data); }
		});
	},

	plot: function(series) {
		trog.chart.series[0].setData(series);
	},

}

$(function(){trog.init();});

