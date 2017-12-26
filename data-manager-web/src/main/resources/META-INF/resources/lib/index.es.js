import { bb, d3 } from 'billboard.js/dist/billboard';

export default function(dataURL, portletNamespace) {
    
//    console.log('dataURL = ' + dataURL);
//    console.log('portletNamespace = ' + portletNamespace);
    
    var chart = bb.generate({
        bindto: `#${portletNamespace}-JSONData`,
        data: {
            
            url: dataURL,
            mimeType: 'json',
            keys: {
                // x: 'name', // it's possible to specify 'x' when category axis
                value: ['value']
            }
        },
        axis: {
            x: {
                // type: 'category'
            }
        }
    });

}