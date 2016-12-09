THIS IS A DRAFT

**Note:** The behavior described here is likely to be a superset of what will be available to the user (but the internal structures should support all these operations).

**TODO:**
- number of structures feature in criteria (should constraints returns multiple coordinates for reporting multiple structures?)
- negating a criterion
- score/result propagation for criterion references
- expliciting the criterion result?
- merge AND and OR criteria into Combining (with a combining operation)?

### World
Consists of:
- a minecraft version
- a seed
- a world generator (i.e. a world type)
- world generator options (optional)
- a center point (all coordinates and areas will be taken relative to this center point)


### Predicate
Given a world and a coordinate, returns whether the coordinate satisfy a condition.

**Example:**
- being in a given biome
- being the center of a given structure
- being inside a given area (square, circle, etc)
- combinations of the above

**Note:** For ease of implementation, only predicates of the form `(biome1 OR ... OR biomeN) AND (structure1 OR ... OR structureN) AND area` will be expressible.


### Valuation
Given a coordinate, returns a positive value (higher is better)

**Example:**
- a constant valuation
- a higher valuation when the coordinate is closer to a given center point


### Criteria
Given a world, returns whether or not it matches a certain condition.
If it matches, returns a list of pair (criterion, coordinates) giving where each leaf criterion has matched (if it has matched), and a score (a positive number).

The different type of criteria are:

#### A Constraint
The basic type of criterion: all criteria are ultimately made with these.

Consists of:
- a Predicate
- a Valuation

Matches iff there exists one coordinate which satisfy the predicate.

Returns itself and the coordinate with maximum valuation among those which satisfy the predicate. If more than one coordinate have maximum valuation, only one will be returned. The score is the valuation of the returned coordinate.

**Short-circuiting:** When evaluating the constraint, we can stop testing coordinates as soon as we know that no non-tested coordinate will have a better valuation than the current one. (e.g. if the valuation is constant, we can return the first coordinate we find)

#### A reference to another Criterion
Matches iff the specified criterion matches.
    

#### An `and` criterion
Consists of: 
- a list of children criteria


Matches iff all of its children match, and returns the results of all of its children; the score is the sum of the scores of all children, plus the score of the `and` criterion itself (if present).

**Short-circuiting:** If one children criterion doesn't match, we can immediately abort and say the `and` criterion doesn't match.

#### An `or` criterion
Consists of:
- a list of children criteria
- two optional attributes `min` and `minScore` (which are positive numbers, 0 by default)

Matches iff at least `min` children matches AND the sum of their scores is at least `minScore`. Only use criteria with a score of 0 if it is required for the criterion to match.

Returns the results of all of the matched children; the score is the sum of the score of all of the matched children, plus the score of the `or` criterion itself (if present).

**Short-circuiting:** If there is not enough untested criteria for the `or` to match, we can immediately abort. Also, we only test the criteria with a score of 0 only after testing all the others (and the `or` still hasn't matched).

### Search Query
TODO

### Seed List
TODO
