package edu.ub.pis2425.projecte.domain.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;

public class Client {
  private final ClientId id;
  private final String password;
  private final List<Exercise> exercises;
  private final List<Routine> routines;
  private final List<Week> weeks;
  private final HashMap<Day, List<Routine>> dayRoutineMap;

  public Client(ClientId id, String password, List<Exercise> exercises, List<Routine> routines, List<Week> weeks, HashMap<Day, List<Routine>> dayRoutineMap) {
    this.id = id;
    this.password = password;
    this.exercises = exercises != null ? new ArrayList<>(exercises) : new ArrayList<>();
    this.routines = routines != null ? new ArrayList<>(routines) : new ArrayList<>();
    this.weeks = weeks != null ? new ArrayList<>(weeks) : new ArrayList<>();
    this.dayRoutineMap = dayRoutineMap != null ? new HashMap<>(dayRoutineMap) : new HashMap<>();
  }

  public Client(Client client) {
    this(client.id, client.password, client.exercises, client.routines, client.weeks, client.dayRoutineMap);
  }

  public Client() {
    this(null, null, null, null, null, null);
  }

  public ClientId getId() {
    return id;
  }

  public String getPassword() {
    return password;
  }

  public List<Exercise> getExercises() {
    return new ArrayList<>(exercises);
  }

  public List<Routine> getRoutines() {
    return new ArrayList<>(routines);
  }

  public List<Week> getWeeks() {
    return new ArrayList<>(weeks);
  }

  public HashMap<Day, List<Routine>> getDayRoutineMap() {
        return new HashMap<>(dayRoutineMap);
    }

  public void setExercises(List<Exercise> exercises) {
    this.exercises.clear();
    this.exercises.addAll(exercises);
  }

  public void setRoutines(List<Routine> routines) {
    this.routines.clear();
    this.routines.addAll(routines);
  }

  public void setWeeks(List<Week> weeks) {
    this.weeks.clear();
    this.weeks.addAll(weeks);
  }

  public List<Routine> getRoutinesFromDay(Day day) {
    return dayRoutineMap.get(day);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Client client = (Client) obj;
    return id.equals(client.id);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  public static Client createClient(String id, String password, String passwordConfirmation, LocalDate registrationDate) {
    if (id.isEmpty())
      throw new IllegalArgumentException("El correo electrónico no puede estar vacío");
    else if (password.isEmpty())
      throw new IllegalArgumentException("La contraseña no puede estar vacía");
    else if (passwordConfirmation.isEmpty())
      throw new IllegalArgumentException("La confirmación de la contraseña no puede estar vacía");
    else if (!password.equals(passwordConfirmation))
      throw new IllegalArgumentException("Las contraseñas no coinciden");

    ClientId clientId = new ClientId(id);
    List<Week> weeks = new ArrayList<>();
    Week initialWeek = Week.create(registrationDate);
    weeks.add(initialWeek);


    return new Client(clientId, password, null, null, weeks, null);
  }
}