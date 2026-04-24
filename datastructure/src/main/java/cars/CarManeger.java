package cars;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CarManeger {
    private Map<Long, Car> cars = new HashMap<>();
    private static CarManeger _intrnce;

    public static CarManeger getInstance() {
        if (_intrnce == null) {
            _intrnce = new CarManeger();
        }
        return _intrnce;
    }

    public static void despose() {
        _intrnce = null;
    }

    public Map<Long, Car> getCars() {
        return Collections.unmodifiableMap(cars);
    }

    public void addCar(Car car) {
        cars.put(car.getID(), car);
    }

    

    
}