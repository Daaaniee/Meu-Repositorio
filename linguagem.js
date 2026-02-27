function linguagemDosGatosECachorros(animal) {
  if (animal === 'cachorro') {
    return 'au au';
  }

  if (animal === 'gato') {
    return 'miau';
  }

  return 'Informe gato ou cachorro';
}

module.exports = { linguagemDosGatosECachorros };
