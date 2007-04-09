// A simple script to test the scripting engine.
// FIXME: Event handlers don't currently seem to throw away their scopes.
// Might have something to do with closures?

//var x = 2;

function thudEventTest (arg) {
	if (typeof(x) == "undefined") {
		x = 1;
	} else {
		x = x + 1;
	}

	return x * arg;
}

// The (in)famous recursive factorial example.
function factorial (n) {
	if (n <= 1)
		return 1;

	return n * factorial (n - 1);
}
