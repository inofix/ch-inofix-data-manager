import { bb, d3 } from 'billboard.js/dist/billboard';

export default function(dataURL, portletNamespace) {
    
    var minDate = "2017-12-27T00:00:00";
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
//            "labels": {
//                "format": {
//                    "value": function (x) {
//                        return d3.format('$')(x)
//                    }
//                }
//            },
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
                    "fit": true,
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
                "title": function (d) { return '' + d; },
                "value": function (value, ratio, id) {
                    var format = id === 'timestamp' ? d3.format('%Y-%m-%d') : d3.format('');
                    return format(value);
                }
            }
        }
    });

}
