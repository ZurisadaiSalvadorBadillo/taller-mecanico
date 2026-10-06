import { buscaCP, buscaColonia, esCPValido } from '@webrek/mx-cp'
import { mexicoStates } from '../data/locationCatalog.js'

function locationId(location) {
  return `${location.cveEnt}|${location.cp}|${location.nombre}`
}

function toNeighborhood(location) {
  return {
    id: locationId(location),
    stateCode: location.cveEnt,
    state: location.estado,
    municipality: location.municipio,
    neighborhood: location.nombre,
    neighborhoodType: location.tipo,
    postalCode: location.cp
  }
}

export function getStates() {
  return mexicoStates
}

export function isMexicanPostalCode(postalCode) {
  return esCPValido(postalCode)
}

export async function findLocationByPostalCode(postalCode) {
  const result = await buscaCP(postalCode)
  if (!result) return null

  return {
    stateCode: result.cveEnt,
    state: result.estado,
    municipality: result.municipio,
    postalCode: result.cp,
    neighborhoods: result.asentamientos.map((settlement) => toNeighborhood({ ...settlement, ...result }))
  }
}

export async function searchNeighborhoods(stateCode, query) {
  if (query.trim().length < 3) return []
  const matches = await buscaColonia(query, { cveEnt: stateCode, limite: 30 })
  return matches.map(toNeighborhood)
}
