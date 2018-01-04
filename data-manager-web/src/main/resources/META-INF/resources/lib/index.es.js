import { bb, d3 } from 'billboard.js/dist/billboard';

export default function(dataURL, portletNamespace) {
       
    var minDate = "2018-01-04T00:00:00";
    var name = "Wassertemperatur";
    var unit = "C°";
    
    var chart = bb.generate({
        bindto: `#${portletNamespace}-JSONData`,
        data: {
       
            xFormat: "%Y-%m-%dT%H:%M:%S",
            "x": "timestamp",
            url: dataURL,
            keys: {
                value: ['timestamp','value']
            },
            mimeType: 'json',
            "names": {
                "value": name,
            },
            "type": "area"
//            "type": "area-step"
//            "type": "bar"
        },
        axis: {
            "x": {
                "min": minDate,
                "type": "timeseries",
                "tick": {
                    "count": 4,
                    "format": "%Y-%m-%d"
                }
            },
            "y": {
                "label": unit,
                "position": "outer-middle"
            }
        },
        "tooltip": {
            "format": {
                "title": function (timestamp) { 
                    var format = d3.timeFormat("%Y-%m-%d %H:%M");
                    return format(timestamp) 
                },
                "value": function (value, ratio, id) {
                    var format = d3.format('');
                    return format(value);
                }
            }
        }
    });

}
