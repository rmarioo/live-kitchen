//Load express module with `require` directive
var express = require('express')
var app = express()

var names = ['Cannavacciuolo',
    'Bottura',
    'Barbieri']

function isNumeric (n) {
    return !isNaN(parseFloat(n)) && isFinite(n)
}

app.get('/executeStep', function (req, res) {
    var recipeStep = {}
    var randomId = Math.floor(Math.random() * names.length)
    recipeStep.preparedBy = names[randomId]
    recipeStep.description= req.query.description
    recipeStep.delay= 0
    var delay = req.query.delay
    if (delay && isNumeric(delay)) {
        recipeStep.delay = delay
        setTimeout(function () {
            res.send(recipeStep)
        }, delay)
    } else res.send(recipeStep)
})

app.listen(9992, function () {
    console.log('app listening on port 9992!')
})

