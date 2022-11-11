big11 test:
	heuristic: cost = 3.3969307170000005, 1 milliseconds
	backtrack: cost = 1.3566775349999998, 111 milliseconds
	mine: cost = 1.3566775349999998, 14 milliseconds

	heuristic: cost = 3.3969307170000005, 1 milliseconds
	backtrack: cost = 1.3566775349999998, 97 milliseconds
	mine: cost = 1.3566775349999998, 14 milliseconds

big12 test:
	heuristic: cost = 1.8271134757999998, 1 milliseconds
	backtrack: cost = 1.3611004867999998, 324 milliseconds
	mine: cost = 1.4875407608, 66 milliseconds
	
	heuristic: cost = 1.8271134757999998, 1 milliseconds
	backtrack: cost = 1.3611004867999998, 322 milliseconds
	mine: cost = 1.4875407608, 89 milliseconds

boralus test:
	heuristic: cost = 183.86, 1 milliseconds
	backtrack: cost = 183.86, 6 milliseconds
	mine: cost = 183.86, 1 milliseconds
	
	heuristic: cost = 183.86, 1 milliseconds
	backtrack: cost = 183.86, 6 milliseconds
	mine: cost = 183.86, 2 milliseconds
	
After implementing some pruning to mine BACKTRACK traverse
i was able to reduce the number of calls to the minehelper function
by almost half on most mtx. This helped me reduce greatly the 
amount of time the mine traverse takes.
