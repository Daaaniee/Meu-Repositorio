import java.util.Locale;
import java.util.Objects;

/**
 * Serviço responsável por retornar o som de um animal suportado.
 *
 * <p>Boas práticas de QA aplicadas:
 * <ul>
 *   <li>Entrada normalizada (trim + case-insensitive)</li>
 *   <li>Validação defensiva de nulo e vazio</li>
 *   <li>Lógica centralizada em enum para reduzir erros de string literal</li>
 * </ul>
 */
public final class AnimalSoundService {

    private static final String INVALID_MESSAGE = "Informe gato ou cachorro";

    public String getSoundByAnimal(String animal) {
        if (animal == null || animal.isBlank()) {
            return INVALID_MESSAGE;
        }

        final String normalizedAnimal = animal.trim().toLowerCase(Locale.ROOT);
        return SupportedAnimal.from(normalizedAnimal)
                .map(SupportedAnimal::sound)
                .orElse(INVALID_MESSAGE);
    }

    private enum SupportedAnimal {
        CACHORRO("cachorro", "au au"),
        GATO("gato", "miau");

        private final String name;
        private final String sound;

        SupportedAnimal(String name, String sound) {
            this.name = Objects.requireNonNull(name, "name não pode ser nulo");
            this.sound = Objects.requireNonNull(sound, "sound não pode ser nulo");
        }

        String sound() {
            return sound;
        }

        static java.util.Optional<SupportedAnimal> from(String input) {
            for (SupportedAnimal animal : values()) {
                if (animal.name.equals(input)) {
                    return java.util.Optional.of(animal);
                }
            }
            return java.util.Optional.empty();
        }
    }
}
