var report = 
%s
;

function main(data) {
    console.log(data);
    var table = document.getElementById("table").getElementsByTagName('tbody')[0];

    document.title = data.reportName;
    document.getElementById("reportName").innerHTML = data.reportName;
    document.getElementById("date").innerHTML = data.date;
    document.getElementById("reportDuration").innerHTML = data.duration;

    data.reportRows.forEach(function(scenarioStat, i) {
      var row = table.insertRow();
      row.id = i;
      var cell1 = row.insertCell(0);
      var cell2 = row.insertCell(1);
      var cell3 = row.insertCell(2);
      cell1.innerHTML = scenarioStat.name;
      cell2.innerHTML = scenarioStat.status;
      cell3.innerHTML = scenarioStat.duration;

      cell1.className = 'test-result-step-command-cell'

     	if (scenarioStat.status === 'passed') {
        cell2.className = 'test-result-step-result-cell-ok'
      } else {
        row.className = 'clickable'
        cell2.className = 'test-result-step-result-cell-failure'
        var errorLogAsString;
        if(scenarioStat.errorLog){
          errorLogAsString = scenarioStat.errorLog.join("\n");                    
        }else{
          errorLogAsString = "Error log was not found.";                    
        }          
        createErrorRow(table, `error_${i}`, errorLogAsString)

        row.onclick = function() {
          var rowId = this.id;
          if (document.getElementById(`error_${rowId}`)) {
            var currentDisplay = document.getElementById(`error_${rowId}`).style.display;
            document.getElementById(`error_${rowId}`).style.display = (currentDisplay === 'none' ? "" : "none");
          }
        };          
        
      }

      cell3.className = 'test-result-step-command-cell'
    });
  }

  function showChart(data) {

    var total = data.total;
    var passed = (data.passed > 0 ? Math.round((data.passed * 100) / total) : 0);
    var pending = (data.pending > 0 ? Math.round((data.pending * 100) / total) : 0);
    var failed = (data.failed > 0 ? Math.round((data.failed * 100) / total) : 0);

    var ctx = document.getElementById("myChart").getContext('2d');
    var myChart = new Chart(ctx, {
      type: 'pie',
      data: {
        labels: [`Passed ${passed} %`, `Failed ${failed} %`, `Pending ${pending} %`],
        datasets: [{
          backgroundColor: [
            "#2ecc71",
            "#ff2c2c",
            "#95a5a6"
          ],
          data: [passed, failed, pending]
        }]
      }
    });
  }
  
  function createErrorRow(table, rowId, errorText) {
    var errorRow = table.insertRow();
    errorRow.id = rowId;
    errorRow.style = "display:none"
    var errorCell = errorRow.insertCell();
    errorCell.setAttribute("colspan", 3);
    errorCell.className = 'error'
    var pre = document.createElement("pre");
    pre.textContent = errorText;
    errorCell.appendChild(pre);
  }
  

  main(report);
  showChart(report);