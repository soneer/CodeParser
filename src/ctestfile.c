#include <stdio.h>

//tests for parenthesis
() (} {) ) (

//tests for brackets
[] [} {] ] [

//tests for curly braces
{} {] [} } {

//tests for double quotes
"This line should be fine."

"This line is not fine.

Neither is this line."

//tests for single quote
'This line should be fine.'

'This line is not fine.

Neither is this line.'

//testing for opening and closing comment proper matches, continued at the end
/*This line here should be fine*/

This line should not be fine*/

//tests for (#ifdef|#ifndef|#if defined|#if!defined)/[#else]/[#endif]
#ifdef TEST
#else THISISFINE
#endif

#ifndef THISISFINE
#endif

#if defined THISISFINE
#endif

#if! defined THISISFINE
#endif

#else THISISNOTFINE

#endif THISISNOTFINE

#ifdef MISSING#ENDIF

int main(void)
{
	printf("Programming is fun.\n");
	
	return 0;
}

//continuation of tests for opening and closing comment proper matches
/*This line is not fine.
