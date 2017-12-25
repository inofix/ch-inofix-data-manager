import * as d3 from "d3";

export default function() {

     // D3 Selection API. Note: it attaches the
     // callbacks to each element in the selection
     d3.selectAll('.hover-me')
     .on('mouseover', function() {
         this.style.backgroundColor = 'yellow';
     })
     .on('mouseleave', function() {
         this.style.backgroundColor = '';
     });

}