#ifndef DEF_HOOK
#define DEF_HOOK

#include <stm32f4xx_hal.h>
#include "Executable.h"

/**
 * Classe m�re abstraite des Hooks.
 */


class Hook
{

public:
	virtual bool evalue() = 0;
	Hook(uint8_t id, bool isUnique, uint8_t nbCallback);
	virtual ~Hook();
	void insert(Executable* f, uint8_t indice); // n'est pas surcharg�, donc pas besoin de la mettre en virtuel
	bool execute(); // idem
	uint16_t getId(); // idem

private:
	uint8_t m_id;

protected:
	bool m_isUnique;

private:
	uint8_t m_nbCallback;
	Executable** m_callbacks;
};

class HookTemps : public Hook
{
private:
	uint32_t m_dateExecution;
	static uint32_t m_dateDebutMatch;

public:
	static void setDateDebutMatch();
	HookTemps(uint8_t id, uint8_t nbCallback, uint32_t dateExecution);
	bool evalue();
};

class HookContact : public Hook
{
private:
	uint8_t m_nbCapteur;

public:
	HookContact(uint8_t id, bool isUnique, uint8_t nbCallback, uint8_t nbCapteur);
	bool evalue();
};

class HookPosition : public Hook
{
private:
	uint16_t m_x;
	uint16_t m_y;
	uint16_t m_tolerance;

public:
	HookPosition(uint8_t id, uint8_t nbCallback, uint16_t x, uint16_t y, uint16_t tolerance);
	bool evalue();
};

class HookDemiPlan : public Hook
{
private:
	float m_x;
	float m_y;
	float m_direction_x;
	float m_direction_y;

public:
	HookDemiPlan(uint8_t id, uint8_t nbCallback, float x, float y, float direction_x, float direction_y);
	bool evalue();
};

#endif
