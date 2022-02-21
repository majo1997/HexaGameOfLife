public class Triangle {
    private double ax, ay;
    private double bx, by;
    private double cx, cy;
    private double px, py;

    /**
     *
     * @param ax    x-coordinate of point a
     * @param ay    y-coordinate of point a
     * @param bx    x-coordinate of point b
     * @param by    y-coordinate of point b
     * @param cx    x-coordinate of point c
     * @param cy    y-coordinate of point c
     * @param px    x-coordinate of point p
     * @param py    y-coordinate of point p
     */
    public Triangle(double ax, double ay, double bx, double by, double cx, double cy, double px, double py) {
        this.ax = ax;
        this.ay = ay;
        this.bx = bx;
        this.by = by;
        this.cx = cx;
        this.cy = cy;
        this.px = px;
        this.py = py;
    }

    /**
     * Returns total length between two passed points
     * @param ax    x-coordinate of point a
     * @param bx    x-coordinate of point b
     * @param ay    y-coordinate of point a
     * @param by    y-coordinate of point b
     * @return  total length between points a and b
     */
    public double length(double ax, double bx, double ay, double by) {
        return Math.sqrt(Math.pow(ax - bx, 2) + Math.pow(ay - by, 2));
    }

    /**
     * returns area of triangle of passed points
     * @param ax    x-coordinate of point a
     * @param ay    y-coordinate of point a
     * @param bx    x-coordinate of point b
     * @param by    y-coordinate of point b
     * @param cx    x-coordinate of point c
     * @param cy    y-coordinate of point c
     * @return  total calculated area
     */
    public double getArea(double ax, double ay, double bx, double by, double cx, double cy) {
        double area = (length(ax, bx, ay, by) + length(bx, cx, by, cy) + length(cx, ax, cy, ay)) / 2;
        return Math.sqrt(
                area * (area - length(ax, bx, ay, by)) * (area - length(bx, cx, by, cy)) * (area - length(cx, ax, cy, ay))
        );
    }

    /**
     * This method returns whether on not the triangle contains the point
     * @return true if triangle contains point else return false
     */
    public boolean contains() {
        double eps = 0.001;

        double areas = getArea(ax, ay, bx, by, px, py) + getArea(ax, ay, px, py, cx, cy) + getArea(px, py, bx, by, cx, cy);

        return Math.abs(areas - getArea(ax, ay, bx, by, cx, cy)) < eps;
    }
}
