/**
 *
 * @author Christian Berndt
 * @created 2017-12-19 12:31
 * @modified 2018-01-16 13:16
 * @version 1.0.2
 *
 */

import { bb, d3 } from 'billboard.js/dist/billboard';

export default function(dataURL, parameters) {
       
    var minDate = new Date(now - 1000 * 60 * 60 * 24 * 7);      // 1 week
    var now = new Date().getTime(); 
    var name = parameters.name;
    var portletNamespace = parameters.namespace;
    var range = parameters.range;
    var unit = parameters.unit; 
    
    if ("day" === range) {
        minDate = new Date(now - 1000 * 60 * 60 * 24);          // 1 day
    } else if ("week" === range) {
        minDate = new Date(now - 1000 * 60 * 60 * 24 * 7);      // 1 week        
    } else if ("month" === range) {
        minDate = new Date(now - 1000 * 60 * 60 * 24 * 30);     // 1 month        
    } else if ("year" === range) {
        minDate = new Date(now - 1000 * 60 * 60 * 24 * 365);    // 1 week        
    }
        
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
