import java.awt.geom.Line2D;

/**
 * Class for Vector object
 * a 'Vector', is a 2 dimensional object, consisting of two doubles
 * Each double represents a component of x or y, for example a Vector (4, 3) would mean 4 in the x direction, and 3 in the y direction
 * Any location on a 2d plane can be represented using a Vector
 */
class Vector {
    private double x, y;
    Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    /**
     * Function to add two Vectors together
     * Adds the x and y components together, and returns the resultant Vector
     * @param v2        - Vector to be added
     * @return      - Resultant vector
     */
    private Vector add(Vector v2){
        double xA = this.x + v2.getX();
        double yA = this.y + v2.getY();
        return new Vector(xA, yA);
    }

    /**
     * Function to subtract one Vector from another
     * Subtracts the Vector passed in from the Vector referenced
     * @param v2        - The vector to be subtracted
     * @return      - The resultant Vector
     */
    Vector subtract(Vector v2){
        double xA = this.x - v2.getX();
        double yA = this.y - v2.getY();
        return new Vector(xA, yA);
    }

    /**
     * Function to get the magnitude of the Vector
     * Uses Pythagoras's theorem treating the Vector components as a triangle, with the hypotenuse as the magnitude
     * @return      - Magnitude
     */
    double getMagnitude(){
        return (Math.sqrt((x*x) + (y*y)));
    }

    /**
     * Function to get the squared magnitude of the Vector
     * Used to avoid 'slow' Math.sqrt() function
     * @return      - Magnitude Squared
     */
    private double getMagnitudeSquared(){
        return (x*x) + (y*y);
    }

    /**
     * Function to get the shortest distance between a point and a line segment
     * The closest distance between a point and a line is always equal to the normal from the line which intersects the point
     * The function uses vector mathematics to find this normal, and then calculate its magnitude from the line to the point
     * It follows the following procedure:
     * Finds the vector from the start of the task to the point
     * Gets the vector from the start of the segment to the end of the segment
     * Verifies that the line has a length greater than 0
     * Finds the dot product of the line segment and the vector from the start of the line to the point
     * Divides this dot product by the line’s magnitude squared, and store as a decimal
     * Multiplies the length of the line by the decimal, and add this to the start of the line, to get the vector from the origin to the closest point on the line
     * Subtracts this vector from the vector from the origin to the point, giving the shortest vector from the point to line
     * Returns the magnitude of this vector
     * Note, the point is the Vector referenced by 'this'
     * @param start     - Start of segment
     * @param end       - End of segment
     * @return          - Magnitude of joining Vector
     */
    double perpendicularDistanceToLineSeg(Vector start, Vector end){
        Vector startToPoint = this.subtract(start);
        Vector line = end.subtract(start);
        double p = -1;
        if (line.getMagnitudeSquared() != 0){
            p = startToPoint.getDotProduct(line) / line.getMagnitudeSquared();
        }
        if ((p >= 0) && (p <= 1)) {
            Vector d = this.subtract(start.add(line.multiply(p)));
            return d.getMagnitude();
        }
        return 4000;        //Return large number if p not in range
    }

    /**
     * Function to get a line segment, of given length, which intersects another segment, at a given acute angle
     * The intersection needs to be a given fraction across the original segment
     * It follows this procedure:
     * Finds the point on the task where the new line will begin
     * Gets the vector joining the start of the node to the task
     * Converts the joining vector into polar co-ordinates
     * Adds the angle of the polar line to the inputted angle of intersection
     * Finds the x coordinate for the end of the new line
     * Finds the y coordinate for the end of the new line
     * Returns the line joining the start of the line to the end coordinates
     * Note, the start of the given segment is referenced by 'this'
     * @param end       - The end of the given segment
     * @param mult      - The fraction of the way across the segment the new line will intersect
     * @param length    - The length of the new segment
     * @param angle     - The acute angle of intersection
     * @return          - The new line
     */
    Line2D.Double getIntersectingLine(Vector end, double mult, int length, double angle){
        Vector point =(end.subtract(this)).multiply(mult).add(this);
        Vector startToPoint = point.subtract(this);
        angle = Math.toRadians(angle);
        double theta = Math.atan2(startToPoint.getY(), startToPoint.getX());
        double x2, y2, rho = theta + angle;
        x2 = point.getX() - length * Math.cos(rho);
        y2 = point.getY() - length * Math.sin(rho);
        return new Line2D.Double(point.getX(), point.getY(), x2, y2);
    }

    /**
     * Function to find a point which is a given perpendicular distance away from a given line, at a given point
     * Follows a very similar procedure to the 'getIntersectingLine' function, but returns a vector instead of a line
     * @param end       - The end of the given segment
     * @param mult      - The fraction of the way across the new line the normal will intersect
     * @param length    - The length across the normal, where the new vector will be
     * @return          - The location found
     */
    Vector getPerpendicularLineEnd(Vector end, double mult, int length){
        Vector point =(end.subtract(this)).multiply(mult).add(this);
        Vector d = point.subtract(this);
        double theta = Math.atan2(d.getY(), d.getX());
        double x, y, rho = theta + Math.PI / 2;
        x = point.getX() - length * Math.cos(rho);
        y = point.getY() - length * Math.sin(rho);
        return new Vector(x, y);
    }

    /**
     * Finds the squared magnitude of a vector which joins two vector locations
     * Uses Pythagoras's theorem
     * @param v2        - Second Vector
     * @return          - Joining magnitude squared
     */
    double getJoiningVectorMagnitudeSquared (Vector v2){
        Vector vJoining = v2.subtract(this);
        return (vJoining.getX() * vJoining.getX()) + (vJoining.getY() * vJoining.getY());
    }

    /**
     * Function to get the unit vector
     * The unit vector of a vector is a vector of the same direction, but with a magnitude of 1
     * @return      - The unit vector
     */
    Vector getUnitVector(){
        return new Vector(x/this.getMagnitude(),y/this.getMagnitude());
    }

    /**
     * Function to multiply a vector by a double
     * Multiplies both of the vector's components by the multiple
     * @param n     - Multiple
     * @return      - The multiplied Vector
     */
    Vector multiply (double n) {
        return new Vector(x * n, y * n);
    }

    /**
     * Function to get the dot product of two vectors
     * The dot product is equal to the sum of the components of the two vectors multiplied together
     * @param v     - The second Vector
     * @return      - Dot product of the Vector
     */
    private double getDotProduct (Vector v){
        return (x*v.getX()) + (y*v.getY());
    }

    /**
     * Function to get the x component of the Vector
     * @return      - x component
     */
    private double getX() {
        return x;
    }

    /**
     * Function to get the y component of the Vector
     * @return      - y component
     */
    private double getY() {
        return y;
    }

    /**
     * Function to get the x component of the Vector rounded to the nearest integer
     * @return      - rounded x component
     */
    int getIntX(){
        return (int) Math.round(x);
    }
    /**
     * Function to get the y component of the Vector rounded to the nearest integer
     * @return      - rounded y component
     */
    int getIntY(){
        return (int) Math.round(y);
    }

    /**
     * Procedure to set x
     * @param x     - New value for x
     */
    void setX(double x){
        this.x = x;
    }
    /**
     * Procedure to set y
     * @param y     - New value for y
     */
    void setY(double y){
        this.y = y;
    }
}
