package CarSimulator;

import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;
import obstacle.CarObstacle;

public class CarSensor {
	Car car;

	public CarSensor(Car car) {
		this.car = car;
	}

	public synchronized double getDistToBounds(CarMap map, double x, double y) {
		double ret = 1e+5;
		Polygon poly = map.road;
		for (int i = 0, j = poly.npoints - 1; i < poly.npoints; j = i++) {
			Line2D.Double l1 = new Line2D.Double(poly.xpoints[i],
					poly.ypoints[i], poly.xpoints[j], poly.ypoints[j]);
			ret = Math.min(ret, l1.ptSegDist(x, y));
		}
		for (int i = 0; i < map.obstacles.size(); i++) {
			if (map.obstacles.get(i) instanceof CarObstacle) {
				CarObstacle co = (CarObstacle) map.obstacles.get(i);
				if (co.car == this.car)
					continue;
			}
			ret = Math.min(ret, map.obstacles.get(i).getDistToBounds(x, y));
		}
		return ret;
	}

	public synchronized Point2D.Double getNearestPoint(CarMap map, double x, double y,
			double theta) {
		Point2D.Double ret = new Point2D.Double();
		ret.x = x;
		ret.y = y;
		double mn = 1e+5;
		Polygon poly = map.road;
		for (int i = 0, j = poly.npoints - 1; i < poly.npoints; j = i++) {
			Line2D.Double l1 = new Line2D.Double(poly.xpoints[i],
					poly.ypoints[i], poly.xpoints[j], poly.ypoints[j]);
			Line2D.Double l2 = new Line2D.Double(x, y, x + Math.cos(theta)
					* 1e+5, y + Math.sin(theta) * 1e+5);
			if (l1.intersectsLine(l2)) {
				double l = 0, r = 1e+5, mid;
				int iteratorCnt = 0;
				while (iteratorCnt++ < 20) {
					mid = (l + r) / 2;
					l2.setLine(x, y, x + Math.cos(theta) * mid,
							y + Math.sin(theta) * mid);
					if (l1.intersectsLine(l2))
						r = mid;
					else
						l = mid;
				}
				mn = Math.min(mn, l);
			}
		}
		for (int i = 0; i < map.obstacles.size(); i++) {
			if (map.obstacles.get(i) instanceof CarObstacle) {
				CarObstacle co = (CarObstacle) map.obstacles.get(i);
				if (co.car == this.car)
					continue;
			}
			Point2D.Double p = map.obstacles.get(i)
					.getNearestPoint(x, y, theta);
			mn = Math.min(mn, Math.hypot(p.x - x, p.y - y));
		}
		ret.x = ret.x + Math.cos(theta) * mn;
		ret.y = ret.y + Math.sin(theta) * mn;
		return ret;
	}
}
